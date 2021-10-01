#!/bin/sh

./gradlew clean
./gradlew jlink

if [ ! -d "/opt/BillApplication" ]; then
  sudo mkdir /opt/BillApplication
  sudo chmod 1777 /opt/BillApplication
fi

if [ ! -d "/opt/BillApplication/files" ]; then
  sudo mkdir /opt/BillApplication/files
  sudo touch /opt/BillApplication/files/accounts.csv
  echo "ID,Name,Address,Contact,Email,Phone,CA" | sudo tee /opt/BillApplication/files/accounts.csv
  sudo touch /opt/BillApplication/files/bills.csv
  sudo echo "ID,Number,AccountID,Type,Date,Amount,Credited,VersionPDF" | sudo tee /opt/BillApplication/files/bills.csv
  sudo touch /opt/BillApplication/files/missions.csv
  sudo echo "ID,Number,Type,Description,Quantity,AccountId,BillId,Price,Date,Billed" | sudo tee /opt/BillApplication/files/missions.csv
  sudo touch /opt/BillApplication/files/settings.csv
  sudo echo "ID,CompanyName,Address,Email,Phone,Logo,Siret,DownloadPath" | sudo tee /opt/BillApplication/files/settings.csv
fi

if [ -d "/opt/BillApplication/build" ]; then
  sudo cp /opt/BillApplication/build/image/bin/accounts.csv /opt/BillApplication/files
  sudo cp /opt/BillApplication/build/image/bin/bills.csv /opt/BillApplication/files
  sudo cp /opt/BillApplication/build/image/bin/missions.csv /opt/BillApplication/files
  sudo cp /opt/BillApplication/build/image/bin/settings.csv /opt/BillApplication/files
  sudo cp -r ./build ./
  sudo cp /opt/BillApplication/files/accounts.csv /opt/BillApplication/build/image/bin/
  sudo cp /opt/BillApplication/files/bills.csv /opt/BillApplication/build/image/bin/
  sudo cp /opt/BillApplication/files/missions.csv /opt/BillApplication/build/image/bin/
  sudo cp /opt/BillApplication/files/settings.csv /opt/BillApplication/build/image/bin/

else
  sudo cp -r ./build /opt/BillApplication
  sudo cp /opt/BillApplication/files/accounts.csv /opt/BillApplication/build/image/bin/
  sudo cp /opt/BillApplication/files/bills.csv /opt/BillApplication/build/image/bin/
  sudo cp /opt/BillApplication/files/missions.csv /opt/BillApplication/build/image/bin/
  sudo cp /opt/BillApplication/files/settings.csv /opt/BillApplication/build/image/bin/
fi

sudo touch ~/Desktop/startBillApplication.sh
sudo echo "sudo /opt/BillApplication/build/image/bin/BillApplication" | sudo tee ~/Desktop/startBillApplication.sh
sudo sudo chmod 1777 ~/Desktop/startBillApplication.sh



