
---
<img align="left" width="250px" src="http://thomasvandenabeele.be/KineFit/logoFIIW.png">
**Thomas Vandenabeele**

3ABA Industriële Ingenieurswetenschappen<br/>
2015-2016

----

# KineFit &mdash; Revalidatie Applicatie

Deze applicatie werd geschreven voor het opleidingsonderdeel **Appontwikkeling voor mobiele platformen** voor de opleiding **Industriële Ingenieurswetenschappen** aan de Universiteit Hasselt.
 
## Initiële voorstel

Voor de taak van *Android Appontwikkeling* zou ik graag een applicatie maken die hoofdzakelijk kan dienen als **pedometer**.
Mijn vader is docent bij de *faculteit geneeskunde* aan de UHasselt en geeft les bij *Revalidatiewetenschappen en kinesitherapie*.
Ze willen voor hun onderzoek graag een applicatie laten ontwikkelen die kan dienen voor de trainingen van de deelnemers van hun onderzoek.

Ik zou graag een applicatie maken waarbij ze hun **stappen, lopen, aantal trappen,..** kunnen **loggen**.
Tevens kunnen de deelnemers hun **trainingen loggen** met de nodige gegevens (dit gebeurt momenteel op papier).
De gegevens worden dan in een database opgenomen. Via de applicatie kunnen ze met grafieken hun voortgang bekijken.
Optioneel kan ik ook een kleine web-based applicatie maken waarmee de onderzoekers deze gegevens kunnen bekijken.

Deze applicatie zou dan kunnen dienen als voorbeeld wat de mogelijkheden zijn van een smartphone applicatie voor hun onderzoek.

## Ondersteunde functies
* REST connectie op webserver (PHP & mysql)
* Eigen thema voor KineFit
* Registreert stappen
* Grafiek met weekoverzicht stappen + andere statistieken
* Taken ontvangen van kinesitherapeuten
* Notificatie bij een nieuwe taak / nieuwe taken
* WebClient waar kinesitherapeuten taken kunnen ingeven
* Logboek voor trainingen en revalidatie oefeningen te loggen

## KineFit Applicatie

<img src="http://thomasvandenabeele.be/KineFit/stap.png" alt="stap" width="200px" border="1">
<img src="http://thomasvandenabeele.be/KineFit/taak.png" alt="taak" width="200px" border="1">
<img src="http://thomasvandenabeele.be/KineFit/logs.png" alt="logging" width="200px" border="1">

Er kan ingelogd worden met volgende admin gebruiker in de applicatie:
```
gebruikersnaam: apps
wachtwoord: android
```

### StapActivity

Hier kunnen gebruikers hun stap statistieken raadplegen. Er kan per week een grafiek bekeken worden van de gelogde stapactiviteiten. Zo kan men ook de dagelijkse, wekenlijkse en het totale aantal stappen raadplegen.

### TaakActivity

Op deze pagina kunnen gebruikers hun openstaande taken bekijken. Met een filtermenu kunnen ook de gesloten en gefaalde taken weergegeven worden of verborgen worden. Door op een open taak te klikken kunnen ze aangeven indien men gefaald of geslaagd is in de taak. De applicatie zoekt om de 10 minuten naar nieuwe taken op de server. Indien er nieuwe taken zijn, ontvangt de gebruiker een notificatie op het toestel. Ook worden nieuwe taken opvallend weergegeven in de lijst van taken. Indien bekeken worden ze als open taken beschouwd, tenzij de gebruiker zelf de status aanpast.

### LogboekActivity

KineFit gebruikers kunnen op deze pagina hun trainingen en andere activiteit die relevant zijn voor het onderzoek loggen. Er zijn verschillende type loggings. De gebruiker kan ook een eigen type aanmaken. Bij elke logging hoort ook een pijn en tevredenheidschaal. Dit kan erg relevant zijn om de revaliderende patiënt op te volgen en is dus erg belangrijk voor het onderzoek.

### Background Services

De applicatie voorziet dus 2 grote services die op de achtergrond van de applicatie lopen. Een kijkt om de 10minuten voor nieuwe taken en genereert de notificaties. De andere luistert naar stap-events en registreert deze in de database. De stap service wordt tevens opgestart bij de boot van het toestel.

## Database

Ik heb geopteerd om de database zo compact mogelijk te houden, doch met zoveel mogelijk structuur. Volgende figuur toont het ER-diagram van mijn gebruikte database:
<br/><img src="http://thomasvandenabeele.be/KineFit/ER.png" alt="ER-diagram" width="500px"><br/>
U kan een blik werpen in mijn database via PHPMyAdmin:
```
http://www.thomasvandenabeele.be/phpmyadmin

gebruikersnaam: apps
wachtwoord: android
```

## REST Client

Om te android applicatie te laten communiceren met de database heb ik een REST API geschreven in PHP. Hiermee kan via HTTP data opgehaald worden of gepost worden in de database. De data kan dan via JSON in de applicatie uitgelezen worden.
De documentatie van de volledige API is via volgende url te vinden:
```
http://www.thomasvandenabeele.be/KineFit/doc/
```

## WebClient

Op vraag van het onderzoeksteam voorzie ik een kleine web applicatie waarop de onderzoekers overzichtelijk belangrijke data kunnen verzamelen van de patiënten. 
<br/><img src="http://thomasvandenabeele.be/KineFit/webclient.png" alt="KineFit WebClient"><br/>
Om wille van de korte tijd heb ik enkel de essentiële basis hierin geintegreerd. Zo kunnen onderzoekers momenteel enkel taken toekennen aan gebruikers. De gebruiker krijgt dan binnen de 10 minuten een melding van de nieuwe taak. Enkel admin gebruikers kunnen momenteel inloggen op deze webclient.
De KineFit WebClient is voor admins te bereiken via volgende url (dezelfde inloggegevens als in applicatie):
```
http://www.thomasvandenabeele.be/KineFit/web/
```

## Verbeteringen voor toekomst

1. **Stapregistratie verbeteren:** Momenteel loopt dit nog wat stroef, via testen heb ik ook ondervonden dat de stappen sensor API op verschillende toestellen heel wat anders kan reageren.
2. **Layout:** Hier en daar springt er soms tekst achter andere UI elementen. Een betere schikking van de opmaak is eventueel nodig.
3. **Webclient:** De onderzoekers zouden graag data willen kunnen verzamelen en grafieken raadplegen van de pijn/tevredenheid scores in functie van de tijd.
