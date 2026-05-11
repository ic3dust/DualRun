$u = "IVRUS\vibertest"
$p = "12345678" | ConvertTo-SecureString -AsPlainText -Force
$c = New-Object System.Management.Automation.PSCredential($u, $p)
$w = "C:\Users\vibertest\AppData\Local\Viber\Viber.exe"

Start-Process -FilePath $w -Credential $c -WorkingDirectory (Split-Path $w)
