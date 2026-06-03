$Root = Split-Path -Parent $PSScriptRoot
$Desktop = [Environment]::GetFolderPath("Desktop")
$Shell = New-Object -ComObject WScript.Shell

function New-Shortcut($Name, $Target, $Icon) {
    $ShortcutPath = Join-Path $Desktop $Name
    $Shortcut = $Shell.CreateShortcut($ShortcutPath)
    $Shortcut.TargetPath = $Target
    $Shortcut.WorkingDirectory = $Root
    if (Test-Path $Icon) {
        $Shortcut.IconLocation = $Icon
    }
    $Shortcut.Save()
}

$Icones = Join-Path $Root "icones"

New-Shortcut "Prontuario Odontologico.lnk" (Join-Path $Root "ABRIR_PRONTUARIO.vbs") (Join-Path $Icones "prontuario.ico")
New-Shortcut "Encerrar Prontuario.lnk" (Join-Path $Root "FECHAR_PRONTUARIO.vbs") (Join-Path $Icones "encerrar.ico")
New-Shortcut "Fazer Backup do Prontuario.lnk" (Join-Path $Root "BACKUP_PRONTUARIO.vbs") (Join-Path $Icones "backup.ico")
