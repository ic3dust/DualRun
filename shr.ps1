$u = "IVRUS\vibertest"
$p = "12345678"
$sp = ConvertTo-SecureString $p -AsPlainText -Force
$c = New-object System.management.Automation.PSCredential($u,$sp)

$openSharedFolder = {
	Set-Location "C:\vibershared"
	Add-Type -AssemblyName System.Windows.Forms
	$f = New-Object System.Windows.Forms.OpenFileDialog
	$f.AutoUpgradeEnabled = $false
	[void]$f.ShowDialog()
}


Start-Process "powershell.exe" -Credential $c -WorkingDirectory "C:\vibershared" -WindowStyle Hidden -ArgumentList "-Command", $openSharedFolder
