# Bill-Application

This application is a simple JAVA FX application. It let you manage your billing work. You can create Accounts, Bills and Missions.
An Account can have several bills which can have several missions. You can as well follow your revenue (CA) on each Accounts.



## Installing the application

For now the script is only for Linux OS and hasn't been tested on others OS.

You can download the code via : `git clone https://github.com/clemb8/Bill-Application.git`

You need to generate the gradle wrapper : `gradle wrapper`

On Linux OS you can simply execute the package.sh script : `./package.sh`. It will build the Java application in your /opt directory.

You can add an alias to your bash configuration to easily run the app :

`alias bill="cd && /opt/BillApplication/build/image/bin/BillApplication"`




## Running Locally

This application is packaged with gradle, you can use :

`gradle build`

`gradle run`

The project is by default build with some data which is in the /BillApplication_data folder. The application should be running after the last command.
