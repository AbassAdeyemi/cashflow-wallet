# Cashflow Wallet

## Description

Cashflow Wallet is a web application that leverages the tbdex payment infrastructure
to provide wallet functionalities to end users. It simplifies international payments 
by providing a user-friendly web interface that can be used by anyone with the intention
of sending or receiving payments across borders.

## How I addressed the design considerations

- **_Profitability:_** The wallet achieves profitability by charging a fixed amount on each 
transaction. Users are required to fund their wallets with a certain amount of money
that is enough to pay for the transaction fees before attempting to proceed to create an order. 
The fee collection is achieved through a blockchain integration and using a stablecoin known as USDC.
Due to the timeline and scope of the project, the actual blockchain integration is not currently
available in the app but there is a fund wallet button on the dashboard that simulates
the adding of funds.
- **_Optionality:_** The wallet handles matching offerings by allowing users to select
their choice from a pool of offerings that match their selected currency pair. It further
empowers them to select the best of the offerings by arranging them in the most
intuitive order. It sorts the offerings by placing offerings from the highest rated
pfis with the lowest exchange rate at the top and offerings from the lowest rated pfis
with the highest exchange rate at the bottom.
- **_Customer Management:_** The wallet manages customer's decentralized identifiers by
encrypting the portable did information in the database. Storing of the portable did
is important because it is required when importing the bearer did which is used to sign
customers' request for quotes
The wallet's backend provides the frontend with the customer's didUri on registration 
or login which the frontend puts in its local storage. The didUri is then used for subsequent 
customer-centric communications with the wallet's backend. Customers are encouraged 
to download their portable dids on registration because it would be needed to login 
when a customer's didUri has been removed from the local storage. The wallet saves the 
users' verifiable credential in their encrypted jwts and only decrypts the
jwts only to extract users' profile information.
- **_Customer Satisfaction:_** The wallet application tracks customer's satisfaction by providing
a ratings tab where customers can rate a pfi at the end of each transaction. The scoring provided
by each customer contributes to what culminates in the average rating of each pfi. The
average rating of each pfi is factored in the ordering of offerings when customers are presented
with matching offerings. To make the ratings accessible to the customers, the average ratings are also 
clearly illustrated in the form of stars filled based on the numbers accumulated by each pfi.


## Technologies Used

- Languages/Framework: Kotlin, SpringBoot, Javascript, Jquery
- Database: Mongodb

## Running the Application

#### Running with docker

- Pull image from docker hub with this command `docker pull abassadey/cashflow-wallet:latest`
- Navigate to the docker-compose folder and run `docker-compose up -d`.
- Ensure that ports `8082` and `27017` are not in use or configure your application and 
mongodb ports in the compose file accordingly.

#### Running without docker

- Run mvn clean install to install dependencies
- Ensure you have mongodb running
- Run `mvn spring-boot:run`
- You can pass these arguments to configure the ports 
- `-Dspring-boot.run.arguments="--server.port=port --spring.data.mongodb.port=27018"`

## Using the Application

Navigate to [http://localhost/8082/cashflow]() to start using the application

## Future Improvements

- Complete blockchain integration for transaction fee collection in usdc
- Build a more robust payment method management system.
- Leverage decentralized web nodes for storing of credentials and user personal 
information