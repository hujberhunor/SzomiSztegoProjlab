# Telepítési és futtatási útmutató
A projekt fordításához szükség lesz Maven build keretrendszere és Git-re, ezt a csatolt script segítségével telepíthetjük. 

A megkapott `dep-install-script.ps1`-re jobbklikkelve a `run with powershell` opcióra kattintva tudjuk futtatni. 
Amikor inputut kér adjunk neki egy `A` billentyűt. Ezek után települni fog minden függőség majd megnyílik egy cmd ablak. (Ekkor lefut a script és `scoop`-pal telepíti a függőségéket) 

Ebbe az ablakban le fog futni egy `git clone` és a `cd` parancs, így bekerülünk a project directory-ba. 

Itt pedig már fordíthatunk: `mvc compile` és futtathatunk `mvn exec:java`. 

# Tesztek futtatásának menete
