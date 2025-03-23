# TODO
- isConnectedToFungus kiszedése => Mivel csak az utolsó fonalból nőhet tovább fonal (continueHypha)
- Új fv minden Tct typehoz, break után kezelni a keletkező tctokat.
- workflow test
- topic 1
Tectononnak egymásra kell hivatkozniuk:
TectonA → TectonB
TectonB ← TectonA 
Különben nem működik → NEM JÓ!
- topic2
Kétirányú assziciáció van a HYPHA-nál is. 

# Telepítési és futtatási útmutató
A projekt fordításához szükség lesz Maven build keretrendszere és Git-re, ezt kétféle képpen telepíthetjük:
1) A csatolt powershell script segítségével
2) Manuálisan végiggyalogolni a powershell scripten.

1.1) Telepítsés powershell script
A megkapott `dep-install-script.ps1`-re jobbklikkelve a `run with powershell` opcióra kattintva tudjuk futtatni. 
Amikor inputut kér adjunk neki egy `A` billentyűt. Ezek után települni fog minden függőség majd megnyílik egy cmd ablak. 
Ebbe az ablakban lefut a `git clone` és a `cd` parancs, így bekerülünk a prohect directory-ba. 
Itt pedig már fordíthatunk: `mvc compile` és futtathatunk `mvn exec:java`. 

2.1) Manuális telepítés
Egy powershellbe illeszük be ezt a parancsot:
```
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
Invoke-RestMethod -Uri https://get.scoop.sh | Invoke-Expression
```
Ez telepíti a scoop csomagkezelő szoftvert.
Ezzel fogjuk telepíteni a Maven build keretrendszert.

Ugyan ebbe a powershell ablakba: 
```
scoop install main/maven
```
Ezzel telepítjüka Maven build keretrendszert. 

A telepítést verifikálni (hogy megtörtént-e) a `mvn -v` paranccsal tudjuk. 
Hasonló outputot kell kapnunk:
```
PS C:\Users\cloud> mvn -v
Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
Maven home: C:\Users\cloud\scoop\apps\maven\current
Java version: 20.0.2, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk-20
Default locale: en_US, platform encoding: UTF-8
OS name: "windows 10", version: "10.0", arch: "amd64", family: "windows"
```

Majd telepítsük a git-et:
```
scoop install main/git
```

A telepítést verifikálni szintén `git -v` commanddal tudjuk. 
Ennek kimenete hasonló:
```
PS C:\Users\cloud> git -v
git version 2.48.1.windows.1
```

Ezek után nyissunk egy cmd ablakot és futtassuk le:
`git clone https://github.com/hujberhunor/SzomiSztegoProjlab.git && cd SzomiSztegoProjlab"` parancsot. 
Itt pedig már fordíthatunk: `mvc compile` és futtathatunk `mvn exec:java`. 

