$user = Get-LocalUser -Name "DualRun" -ErrorAction SilentlyContinue

try{
    $isAdmin = (
        New-Object Security.Principal.WindowsPrincipal(
            [Security.Principal.WindowsIdentity]::GetCurrent()
        )
    ).IsInRole(
        [Security.Principal.WindowsBuiltInRole]::Administrator
    )

    if($null -ne $user){
        $sid = $user.SID

        # User
        Remove-LocalUser -Name $user.Name -ErrorAction Stop

        # System + registry profile
        Get-CimInstance -Class Win32_UserProfile |
        Where-Object {$_.SID -eq $user.SID } |
        Remove-CimInstance -ErrorAction SilentlyContinue
        Write-Output "DualRun user was deleted successfully"
        exit 0

    }else{
        Write-Output "[ERROR] DualRun user does not exist"
        exit 1
    }

}catch{
    Write-Output "[ERROR] DualRun user removal failed. Try yourself"
    Write-Output $_.Exception.Message
    exit 2
}