Price Administration for Prestashop PHP engine
==============================================

This is a basic java desktop application that will allow you to download and 
edit price information in Excel format from a Prestashop database.

Requirements
------------
* You need to have remote access permisions to the MySQL Prestashop database.
  Most hosts have this feature disabled by default.

* You need git & maven installed


Installation
------------

    git clone git@github.com:bftanase/Price-Administration-for-Prestashop
    
    cd Price-Administration-for-Prestashop
    
    mvn install

run the program using exec plugin
   
    mvn exec:java


Complete the required database connection settings and click `Connect`. If succesfull the `Download Prices`
button will be available.

Clicking `Download Prices` will open an Excel file with all the products and prices defined in the database.

If you change a price in Excel file and **save** the `Upload Prices` button will become available.

Clicking this will upload to the live database the new prices.

