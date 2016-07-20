

<img align="left" width="300px" src="http://thomasvandenabeele.be/KineFit/logoFIIW.png">
**Thomas Vandenabeele**
3ABA Industriële Ingenieurswetenschappen
2015-2016<br/><br/>

----

# KineFit &mdash; Revalidatie Applicatie

Deze applicatie werd geschreven voor het opleidingsonderdeel **Appontwikkeling voor mobiele platformen** voor de opleiding **Industriële Ingenieurswetenschappen** aan de Universiteit Hasselt.

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

<img src="http://thomasvandenabeele.be/KineFit/app.png" alt="stap">

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

## REST Client

Om te android applicatie te laten communiceren met de database heb ik een REST API geschreven in PHP. Hiermee kan via HTTP data opgehaald worden of gepost worden in de database. De data kan dan via JSON in de applicatie uitgelezen worden.
De documentatie van de volledige API is via volgende url te vinden: http://www.thomasvandenabeele.be/KineFit/doc/


## WebClient

Op vraag van het onderzoeksteam voorzie ik een kleine web applicatie waarop de onderzoekers overzichtelijk belangrijke data kunnen verzamelen van de patiënten. 
<br/><img src="http://thomasvandenabeele.be/KineFit/webclient.png" alt="KineFit WebClient"><br/>
Om wille van de korte tijd heb ik enkel de essentiële basis hierin geintegreerd. Zo kunnen onderzoekers momenteel enkel taken toekennen aan gebruikers. De gebruiker krijgt dan binnen de 10 minuten een melding van de nieuwe taak. Enkel admin gebruikers kunnen momenteel inloggen op deze webclient.

## Verbeteringen voor toekomst

1. **Stapregistratie verbeteren:** Momenteel loopt dit nog wat stroef, via testen heb ik ook ondervonden dat de stappen sensor API op verschillende toestellen heel wat anders kan reageren.
2. **Layout:** Hier en daar springt er soms tekst achter andere UI elementen. Een betere schikking van de opmaak is eventueel nodig.
3. **Webclient:** De onderzoekers zouden graag data willen kunnen verzamelen en grafieken raadplegen van de pijn/tevredenheid scores in functie van de tijd.
