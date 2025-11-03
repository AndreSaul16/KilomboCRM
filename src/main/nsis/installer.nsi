;NSIS Installer Script for KilomboCRM
;This script creates a complete installer that includes Java and MySQL setup

!include "MUI2.nsh"
!include "FileFunc.nsh"
!include "LogicLib.nsh"

;General Configuration
Name "KilomboCRM"
OutFile "KilomboCRM-Installer.exe"
Unicode True
InstallDir "$PROGRAMFILES\KilomboCRM"
InstallDirRegKey HKCU "Software\KilomboCRM" ""
RequestExecutionLevel admin

;Modern UI Configuration
!define MUI_ABORTWARNING
;!define MUI_ICON "icon.ico"
;!define MUI_UNICON "icon.ico"
;!define MUI_HEADERIMAGE
;!define MUI_HEADERIMAGE_BITMAP "header.bmp"
;!define MUI_WELCOMEFINISHPAGE_BITMAP "wizard.bmp"

;Pages
!insertmacro MUI_PAGE_WELCOME
;!insertmacro MUI_PAGE_LICENSE "license.txt"
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH

;Languages
!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_LANGUAGE "Spanish"

;Installer Sections
Section "KilomboCRM Application" SecApp
    SectionIn RO

    SetOutPath "$INSTDIR"
    DetailPrint "Copiando archivos de la aplicación..."
    File "KilomboCRM-1.0.0-jar-with-dependencies.jar"
    File "KilomboCRM.bat"

    ;Create desktop shortcut
    CreateShortCut "$DESKTOP\KilomboCRM.lnk" "$INSTDIR\KilomboCRM.bat" "" "$INSTDIR\KilomboCRM.bat" 0

    ;Create start menu entries
    CreateDirectory "$SMPROGRAMS\KilomboCRM"
    CreateShortCut "$SMPROGRAMS\KilomboCRM\KilomboCRM.lnk" "$INSTDIR\KilomboCRM.bat"
    CreateShortCut "$SMPROGRAMS\KilomboCRM\Uninstall.lnk" "$INSTDIR\Uninstall.exe"

    ;Registry information for add/remove programs
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KilomboCRM" "DisplayName" "KilomboCRM"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KilomboCRM" "UninstallString" "$INSTDIR\Uninstall.exe"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KilomboCRM" "QuietUninstallString" "$INSTDIR\Uninstall.exe /S"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KilomboCRM" "InstallLocation" "$INSTDIR"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KilomboCRM" "DisplayIcon" "$INSTDIR\KilomboCRM.bat"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KilomboCRM" "Publisher" "Kilombo Team"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KilomboCRM" "HelpLink" "https://github.com/AndreSaul16/KilomboCRM"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KilomboCRM" "URLUpdateInfo" "https://github.com/AndreSaul16/KilomboCRM"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KilomboCRM" "URLInfoAbout" "https://github.com/AndreSaul16/KilomboCRM"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KilomboCRM" "DisplayVersion" "1.0.0"
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KilomboCRM" "VersionMajor" 1
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KilomboCRM" "VersionMinor" 0
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KilomboCRM" "NoModify" 1
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KilomboCRM" "NoRepair" 1
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KilomboCRM" "EstimatedSize" 102400

    WriteUninstaller "$INSTDIR\Uninstall.exe"
SectionEnd

Section "Java Runtime Environment" SecJava
    ;Check if Java is already installed
    nsExec::ExecToLog '"$SYSDIR\java.exe" -version'
    Pop $0
    ${If} $0 != 0
        MessageBox MB_YESNO "Java no está instalado. ¿Desea descargarlo e instalarlo ahora?" IDYES InstallJava IDNO SkipJava
        InstallJava:
            ;Download and install Java
            NSISdl::download "https://download.oracle.com/java/17/archive/jdk-17.0.9_windows-x64_bin.exe" "$TEMP\jdk-installer.exe"
            nsExec::ExecToLog '"$TEMP\jdk-installer.exe" /s'
            Delete "$TEMP\jdk-installer.exe"
        SkipJava:
    ${EndIf}
SectionEnd

Section "MySQL Server" SecMySQL
    ;Check if MySQL is already installed
    nsExec::ExecToLog '"$PROGRAMFILES\MySQL\MySQL Server 8.0\bin\mysql.exe" --version'
    Pop $0
    ${If} $0 != 0
        MessageBox MB_YESNO "MySQL no está instalado. ¿Desea descargarlo e instalarlo ahora?" IDYES InstallMySQL IDNO SkipMySQL
        InstallMySQL:
            ;Download and install MySQL
            NSISdl::download "https://dev.mysql.com/get/Downloads/MySQL-8.0/mysql-8.0.36-winx64.msi" "$TEMP\mysql-installer.msi"
            nsExec::ExecToLog 'msiexec /i "$TEMP\mysql-installer.msi" /quiet'
            Delete "$TEMP\mysql-installer.msi"
        SkipMySQL:
    ${EndIf}
SectionEnd

;Descriptions
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecApp} "Instala la aplicación KilomboCRM y crea accesos directos."
    !insertmacro MUI_DESCRIPTION_TEXT ${SecJava} "Instala Java Runtime Environment si no está presente en el sistema."
    !insertmacro MUI_DESCRIPTION_TEXT ${SecMySQL} "Instala MySQL Server si no está presente en el sistema."
!insertmacro MUI_FUNCTION_DESCRIPTION_END

;Uninstaller Section
Section "Uninstall"
    Delete "$INSTDIR\KilomboCRM-1.0.0-jar-with-dependencies.jar"
    Delete "$INSTDIR\KilomboCRM.bat"
    Delete "$INSTDIR\Uninstall.exe"

    Delete "$DESKTOP\KilomboCRM.lnk"
    RMDir /r "$SMPROGRAMS\KilomboCRM"

    RMDir "$INSTDIR"

    DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\KilomboCRM"
    DeleteRegKey HKCU "Software\KilomboCRM"
SectionEnd