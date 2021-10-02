#!/bin/sh

HOME=printenv HOME

./gradlew clean
./gradlew jlink

if [ ! -d "/opt/BillApplication" ]; then
  sudo mkdir /opt/BillApplication
  sudo chmod 1777 /opt/BillApplication
fi

if [ ! -d "/opt/BillApplication/files" ]; then
  sudo mkdir /opt/BillApplication/files
  sudo touch /opt/BillApplication/files/accounts.csv
  echo "ID,Name,Address,Contact,Email,Phone,Siren,CA" | sudo tee /opt/BillApplication/files/accounts.csv
  sudo touch /opt/BillApplication/files/bills.csv
  sudo echo "ID,Number,AccountID,Type,Date,Amount,Credited,VersionPDF" | sudo tee /opt/BillApplication/files/bills.csv
  sudo touch /opt/BillApplication/files/missions.csv
  sudo echo "ID,Number,Type,Description,Quantity,AccountId,BillId,Price,Date,Billed" | sudo tee /opt/BillApplication/files/missions.csv
  sudo touch /opt/BillApplication/files/settings.csv
  sudo echo "ID,CompanyName,Address,Email,Phone,Logo,Siret,DownloadPath" | sudo tee /opt/BillApplication/files/settings.csv
fi

if [ -d "$HOME/BillApplication_data/" ]; then
  sudo cp "$HOME/BillApplication_data/accounts.csv" /opt/BillApplication/files
  sudo cp "$HOME/BillApplication_data/bills.csv" /opt/BillApplication/files
  sudo cp "$HOME/BillApplication_data/missions.csv" /opt/BillApplication/files
  sudo cp "$HOME/BillApplication_data/settings.csv" /opt/BillApplication/files
  sudo cp -r ./build /opt/BillApplication/
  sudo cp /opt/BillApplication/files/accounts.csv "$HOME/BillApplication_data/accounts.csv"
  sudo cp /opt/BillApplication/files/bills.csv "$HOME/BillApplication_data/bills.csv"
  sudo cp /opt/BillApplication/files/missions.csv "$HOME/BillApplication_data/missions.csv"
  sudo cp /opt/BillApplication/files/settings.csv "$HOME/BillApplication_data/settings.csv"

else
  mkdir "$HOME/BillApplication_data/"
  sudo cp -r ./build /opt/BillApplication
  sudo cp /opt/BillApplication/files/accounts.csv "$HOME/BillApplication_data/accounts.csv"
  sudo cp /opt/BillApplication/files/bills.csv "$HOME/BillApplication_data/bills.csv"
  sudo cp /opt/BillApplication/files/missions.csv "$HOME/BillApplication_data/missions.csv"
  sudo cp /opt/BillApplication/files/settings.csv "$HOME/BillApplication_data/settings.csv"
fi

sudo chmod 1777 "$HOME/BillApplication_data/accounts.csv"
sudo chmod 1777 "$HOME/BillApplication_data/bills.csv"
sudo chmod 1777 "$HOME/BillApplication_data/missions.csv"
sudo chmod 1777 "$HOME/BillApplication_data/settings.csv"
sudo chown clem8 "$HOME/BillApplication_data/accounts.csv"
sudo chown clem8 "$HOME/BillApplication_data/bills.csv"
sudo chown clem8 "$HOME/BillApplication_data/missions.csv"
sudo chown clem8 "$HOME/BillApplication_data/settings.csv"
