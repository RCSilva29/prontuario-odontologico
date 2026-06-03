Set WshShell = CreateObject("WScript.Shell")
Set FSO = CreateObject("Scripting.FileSystemObject")
Pasta = FSO.GetParentFolderName(WScript.ScriptFullName)
WshShell.Run chr(34) & Pasta & "\scripts\iniciar.bat" & Chr(34), 0
Set FSO = Nothing
Set WshShell = Nothing
