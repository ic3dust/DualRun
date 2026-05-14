$url = "https://download.cdn.viber.com/desktop/windows/ViberSetup.exe"
$path = "$env:TEMP\ViberSetup.exe"
$timeout = 120
$n = 0
$interval = 1

Invoke-WebRequest -Uri $url -OutFile $path

$u = "DualRun"
$p = Read-Host "DualRun#2026" -AsSecureString
$c = New-Object System.Management.Automation.PSCredential($u,$p)
$proc = Start-Process -FilePath $path -Credential $c -Wait -PassThru
if($proc.ExitCode -ne 0){
    throw "Installer failed"
}

$exe = "C:\Users\DualRun\AppData\Local\Viber\Viber.exe"
while (-not (Test-Path $exe)){
    if($n -ge $timeout){
        throw "Timeout: Viber.exe was not found after $timeout s of waiting. Installation may've failed."
    }

    Start-Sleep -Seconds $interval
    $n+= $interval
}

$exe = "C:\Users\DualRun\AppData\Local\Viber\Viber.exe"
Start-Process -FilePath $exe -Credential $c -WorkingDirectory (Split-Path $exe)