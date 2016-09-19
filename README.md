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
