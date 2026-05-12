
$u="DualRun"
$p = "DualRun#2026"

# Checks #
$psv = $PSVersionTable.PSVersion
$cu = whoami
$gc = Get-Command New-LocalUser

Write-Output "PowerShell version: $psv"
Write-Output "PowerShell path: $PSHOME"
Write-Output "Current user: $cu"
Write-Output "Command exists: $($gc.Name) `n`n"

try{

    $isAdmin = (
        New-Object Security.Principal.WindowsPrincipal(
            [Security.Principal.WindowsIdentity]::GetCurrent()
        )
    ).IsInRole(
        [Security.Principal.WindowsBuiltInRole]::Administrator
    )

    Write-Output "[INFO] Running as admin: $isAdmin"


    Write-Output "[INFO] Creating user $u..."

    $exist = Get-LocalUser -Name $u -ErrorAction SilentlyContinue
    if($exist){
        exit 1
    }else{
        $sp = ConvertTo-SecureString $p -AsPlainText -Force
        New-LocalUser -Name $u -Password $sp -FullName "Dual Run" -ErrorAction Stop
        exit 0 
    }
}
catch{
    Write-Output "[FAIL] Failed to create DualRun user: "
    Write-Output $_.Exception.Message

    exit 2
}
