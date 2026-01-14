; ---------------------------------------------------------
; 🎵 Music Player System - Installer Script (Final + Uninstall Page)
; Developed by: Muhammad Mubashir Shafique, Usman, and Meraj Ali
; BSCS Students, Sukkur IBA University (2024–2028)
; Publisher: Mubrix Technology
; ---------------------------------------------------------

#define MyAppName "Music Player System"
#define MyAppVersion "1.0"
#define MyAppPublisher "Mubrix Technology"
#define MyAppExeName "MusicPlayerSystemInstaller.exe"
#define MyAppAssocName "MP3 Music File"
#define MyAppAssocExt ".mp3"
#define MyAppAssocKey StringChange(MyAppAssocName, " ", "") + MyAppAssocExt

[Setup]
AppId={{C0277748-0992-493F-A5AD-FCE4669DC5D9}}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppVerName={#MyAppName} {#MyAppVersion}
PrivilegesRequired=admin
PrivilegesRequiredOverridesAllowed=dialog
DefaultDirName={autopf}\{#MyAppName}
UninstallDisplayIcon={app}\{#MyAppExeName}
ChangesAssociations=yes
DisableProgramGroupPage=no
WizardStyle=modern

LicenseFile=C:\Users\mubas\Desktop\LICENSE.txt
OutputDir=C:\Users\mubas\Desktop\InstallerOutput
OutputBaseFilename=MusicPlayerSystemSetup
SetupIconFile=C:\Users\mubas\Desktop\MusicPlayerSystem\resources\2764.ico
Compression=lzma
SolidCompression=yes
WizardSmallImageFile=C:\Users\mubas\Desktop\MusicPlayerSystem\resources\27648.png

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "Create a &Desktop Shortcut"; GroupDescription: "Additional Icons"; Flags: unchecked

[Files]
Source: "C:\Users\mubas\Desktop\MusicPlayerSystem\{#MyAppExeName}"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\mubas\Desktop\MusicPlayerSystem\jre\*"; DestDir: "{app}\jre"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\Users\mubas\Desktop\MusicPlayerSystem\lib\*"; DestDir: "{app}\lib"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\Users\mubas\Desktop\MusicPlayerSystem\resources\*"; DestDir: "{app}\resources"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\Users\mubas\Desktop\MusicPlayerSystem\Songs"; DestDir: "{app}\Songs"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\Users\mubas\Desktop\MusicPlayerSystem\src\*"; DestDir: "{app}\src"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\Users\mubas\Desktop\MusicPlayerSystem\musicdb.accdb"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\mubas\Desktop\MusicPlayerSystem\Run.jar"; DestDir: "{app}"; Flags: ignoreversion

[Registry]
Root: HKCR; Subkey: "{#MyAppAssocExt}\OpenWithProgids"; ValueType: string; ValueName: "{#MyAppAssocKey}"; ValueData: ""; Flags: uninsdeletevalue
Root: HKCR; Subkey: "{#MyAppAssocKey}"; ValueType: string; ValueName: ""; ValueData: "{#MyAppAssocName}"; Flags: uninsdeletekey
Root: HKCR; Subkey: "{#MyAppAssocKey}\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\{#MyAppExeName},0"
Root: HKCR; Subkey: "{#MyAppAssocKey}\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""%1"""

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon
Name: "{app}\Uninstall {#MyAppName}"; Filename: "{uninstallexe}"

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "Launch {#MyAppName}"; Flags: nowait postinstall skipifsilent shellexec
Filename: "https://mubashirshafique.github.io/MusicPlayerSystem-html/thankyou.html"; Description: "Open Thank You Page"; Flags: shellexec postinstall skipifsilent

[UninstallRun]
; ✅ Automatically open uninstall page after removal
Filename: "https://mubashirshafique.github.io/MusicPlayerSystem-html/uninstall.html"; Flags: shellexec runhidden

[Code]
// Ask user whether to delete user data during uninstall
function InitializeUninstall(): Boolean;
var
  MsgResult: Integer;
  SongsPath, DBPath: String;
begin
  SongsPath := ExpandConstant('{app}\Songs');
  DBPath := ExpandConstant('{app}\musicdb.accdb');

  MsgResult := MsgBox(
    'Your songs and playlists are stored here:' + #13#13 +
    SongsPath + #13#13 +
    'Do you want to delete these files and the database as well?' + #13#13 +
    'Click "Yes" to delete everything, or "No" to keep your data.',
    mbConfirmation, MB_YESNO
  );

  if MsgResult = IDYES then
  begin
    if DirExists(SongsPath) then
      DelTree(SongsPath, True, True, True);
    if FileExists(DBPath) then
      DeleteFile(DBPath);
    MsgBox('All user data (songs, playlists, and database) has been deleted successfully.', mbInformation, MB_OK);
  end
  else
  begin
    MsgBox('Your songs and playlists were kept safe.', mbInformation, MB_OK);
  end;

  Result := True;
end;

