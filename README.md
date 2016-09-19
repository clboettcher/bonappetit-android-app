# bonappetit-android-app
The native Android app client for bonappetit.

Architecture notes and notes on the stuff in this repo

* Wiremock REST API mock: tools/mock-rest-api
* Custom Application class: com.github.clboettcher.bonappetit
.app.application.BonAppetitApplication (see javadoc for details)
* The com.github.clboettcher.bonappetit.app.ConfigProvider provides
config form the shared preferences. It adds additional value by 
* A 'service' (located under com/github/clboettcher/bonappetit/app/service)
is an application component that encapsulate backend requests. It 
typically is not invoked directly. Instead it listens to one or 
more events. The result of the operation is again communicated 
via events. The actual API calls are performed using the retrofit library.
* The com.github.clboettcher.bonappetit.app.service.ApiProvider provides 
instances of those retrofit APIs. See javadocs for why it is needed.

TODO: 

* Trennung der Backend Module von deren API, damit das API Modul auch in der
APP benutzt werden kann (--> StaffMemberDto).
* Einbindung der API-Module (--> StaffMemberDto, MenuDto, etc.) in die App (unter /libs). Atm: manuelles Kopieren.
* Main Fragment fertig machen. Idee: alle Daten vom Backend laden, die benötigt werden:
  Staff members, Menu
  erst dann: anzeige der Kundenauswahl mit Staff members popup (wie gewohnt aus dem POC) Das ist
  grad das einfachste ... 
  * Styles fertig definieren für die Loading Anzeige: Fehlt insb. Style für den App-Title unter Verwendung der dimens.xml
  * Styles fertig definieren für die Fehler-Anzeige.
    Fehler Anzeige vereinfachen. Fehlermeldung + Wiederholen-Button
  * Abruf des Menus vom Server hinterlegen:
    * Mock: /currentMenu - Fetch the current menu from the server.
    * API * GET /currentMenu
    * Service + Events
  * Trigger der "Lade Zeug Events" beim Laden des Fragments / der Activity.
  