Write-Host " Most elkezdődik a telepítés. "

Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser -Force

Invoke-RestMethod -Uri https://get.scoop.sh | Invoke-Expression

scoop install main/maven
scoop install main/git

Write-Host " --- "

$mvnVersion = mvn -v
if ($mvnVersion) {
    Write-Host "Maven sikeresen telepítve:"
    Write-Host $mvnVersion
} else {
    Write-Host "Hiba történt a Maven telepítése során."
}

Write-Host " --- " 

$gitVersion = git -v
if ($gitVersion) {
    Write-Host "Git sikeresen telepítve:"
    Write-Host $gitVersion
} else {
    Write-Host "Hiba történt a Git telepítése során."
}

Write-Host " --- "

$javaVersion = java -version 2>&1
if ($javaVersion) {
    Write-Host "Java verzió ellenőrzése sikeres:"
    Write-Host $javaVersion
} else {
    Write-Host "Java nem található a rendszerben."
}

Write-Host "Készen vagyunk! Most megnyitunk egy új cmd ablakot, ahol klónozzuk a repót..."

Start-Process cmd.exe -ArgumentList "/k git clone https://github.com/hujberhunor/SzomiSztegoProjlab.git && cd SzomiSztegoProjlab"
