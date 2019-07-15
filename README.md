# partybot
бот для записи грядущих событий и дней рождения, с возможностью периодических оповещений в чатах
ВНИМАНИЕ!!!
это не готовый к запуску бот, это код, присоединяемый к Spring-проекту, чтобы его использовать, нужно либо присоединить его к существующему проекту, либо создать новый проект. 

Необходимые зависимости(Maven):

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.telegram</groupId>
            <artifactId>telegrambots-spring-boot-starter</artifactId>
            <version>4.1.2</version>
        </dependency>
