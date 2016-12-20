# BonAppetit Android App

BonAppetit is a free Point-of-Sale solution for small and medium restaurants. It consists of an Android App client (this project) and a 
[Java Server Application](https://github.com/clboettcher/bonappetit) connected to a receipt printer.

The client app provides the ability to record, edit and delete
orders for menu items configured in the server. Orders are transferred to the server via WLAN.

The server saves the recorded orders to a database and prints receipts using a thermal receipt printer. The receipts
contain all information required to further process the order consisting of table number, item name, options,
additional free text notes as well as the service member who took the order and the time the order was registered.

The plan is to merge this project into the server project at one point.

Please refer to the [Server Project](https://github.com/clboettcher/bonappetit) for full documentation and
issue tracking.

## GNU GPL v3 License

BonAppetit is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

BonAppetit is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
[GNU General Public License](LICENSE) for more details.
