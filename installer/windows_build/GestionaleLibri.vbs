' Script VBS per avviare il programma senza finestra nera
Set WshShell = CreateObject("WScript.Shell")
WshShell.CurrentDirectory = CreateObject("Scripting.FileSystemObject").GetParentFolderName(WScript.ScriptFullName)
WshShell.Run "javaw -jar GestionaleLibri.jar", 0, False
