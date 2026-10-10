<h1>UHCMEETUP - MINECRAFT GAME MODE</h1>
<p>
  This game mode was created by myself in 2021 as my first major <b>Java</b> project using the <b>Spigot</b> engine <b>1.8</b>.
</p>

<h2>Game Rules</h2>
<p>
  A set number of players gather on a <b>250x250</b> map.<br>
  Upon starting, every player receives a randomized set of items with balanced stats — each kit offers a unique tactical advantage over the others.<br>
  Once the required number of players join, the game begins automatically.<br>
  The main objective is to eliminate all opponents until only <b>1 player remains</b> as the winner.<br>
  As time goes on, the map border progressively shrinks, forcing close encounters!
</p>

<h2>Tech Stack</h2>
<p>
  <b>Language:</b> Java 8 (JDK 1.8)<br>
  <b>API / Engine:</b> Spigot API 1.8.8<br>
  <b>Build Tool:</b> Maven<br>
</p>

<h2>Architecture & Technical Solutions</h2>
<p>
  <b>1. State Machine & Access Control (State Machine & Lockdown)</b><br>
  Stores the current game state in the <code>isStarted</code> variable and manages the active player list (<code>inGame</code>). Prior to game start, it restricts inventory interactions (<code>onInteract</code>), block-level player movement (<code>onMove</code>), and cancels all incoming damage (<code>onDamage</code>).
</p>
<p>
  <b>2. Countdown Timer & Session Management (Lobby & Task Scheduler)</b><br>
  Upon reaching the threshold of 8 players in <code>onJoin</code>, it initializes a delayed <code>BukkitRunnable</code> task (20 seconds). If a player leaves (<code>onQuit</code>) before start and the player count drops below 8, the task is immediately canceled (<code>startGameTask.cancel()</code>). Reaching the limit of 24 players blocks new connections to the server (<code>onLogin</code> / kick).
</p>
<p>
  <b>3. Dynamic World Shrinking (WorldBorder NMS/Bukkit)</b><br>
  After the initial countdown completes, it reduces the world border radius (<code>WorldBorder</code>) by 250 blocks over 9,600 ticks (8 minutes). The <code>onMove</code> method detects border proximity (10 blocks) and displays real-time warnings on the Actionbar.
</p>
<p>
  <b>4. Kit System & Consumables (Kits & Consumables)</b><br>
  Listens for interactions with an enchanted book in <code>onInteract</code>, assigning armor sets and items based on percentage chances (10%, 15%, 35%, 40%). Includes custom player head consumption logic (<code>Material.SKULL_ITEM</code>), granting Speed II, Regeneration IV, and Absorption I effects.
</p>
<p>
  <b>5. Elimination Validation & Victory Detector (Victory Check)</b><br>
  The <code>onDeath</code> and <code>onQuit</code> methods (during gameplay) handle removing players from the <code>inGame</code> list, awarding kill bonuses, and evaluating victory conditions. Once only 1 player remains in <code>inGame</code>, it triggers stats saving in <code>UserFile</code>, end-game visual effects, and a server restart.
</p>
<p>
  <b>6. Communication Normalization & Screen Packets (Packets & Formatting)</b><br>
  Clears default Bukkit system messages (<code>setJoinMessage("")</code>, <code>setDeathMessage("")</code>) in favor of native NMS packets (<code>PacketPlayOutTitle</code>) and custom chat formatting based on permissions (<code>OP</code>, <code>SPECTATOR</code>, <code>PLAYER</code>).
</p>

<h2>Installation & Configuration</h2>
<p>
  <b>1. Prerequisites</b><br>
  • <b>Java 8 (JDK / JRE 1.8)</b> runtime environment.<br>
  • A Minecraft server running on <b>Spigot 1.8.8</b> or <b>(recommended)</b> <a href="https://papermc.io/downloads/all" target="_blank">PaperSpigot 1.8.8</a>.
</p>
<p>
  <b>2. Building & Moving the Plugin</b><br>
  • Compile the project using <code>mvn clean package</code> or obtain the built artifact.<br>
  • Locate the generated <code>Filipesz-UHCMEETUP-0.0.1.jar</code> file inside the <code>target/</code> directory.<br>
  • Move the <code>.jar</code> file into your Minecraft server's <code>plugins/</code> folder.
</p>
<p>
  <b>3. Execution & Verification</b><br>
  Start or restart your Minecraft server.<br>
</p>

<hr>

<h1>UHCMEETUP - TRYB DO GRY MINECRAFT</h1>
<p>
  Jest to tryb napisany przeze mnie w 2021 roku i zarazem mój pierwszy większy projekt w języku <b>Java</b> na silniku <b>Spigot 1.8</b>.
</p>

<h2>Zasady rozgrywki</h2>
<p>
  Na mapie o wymiarach <b>250x250</b> zbiera się określona liczba graczy.<br>
  Każdy z nich na start otrzymuje losowe przedmioty o zbliżonej konfiguracji — każdy zestaw posiada inny rodzaj przewagi nad pozostałymi.<br>
  Gdy zbierze się wymagana liczba graczy, gra automatycznie się rozpoczyna.<br>
  Rozgrywka polega na eliminowaniu się nawzajem, aż na placu boju pozostanie tylko <b>1 zwycięzca</b>.<br>
  W trakcie gry obszar mapy sukcesywnie się pomniejsza, zmuszając graczy do walki!
</p>

<h2>Stos Technologiczny</h2>
<p>
  <b>Język:</b> Java 8 (JDK 1.8)<br>
  <b>API / Silnik:</b> Spigot API 1.8.8<br>
  <b>Narzędzie budowania:</b> Maven<br>
</p>

<h2>Architektura i Rozwiązania Techniczne</h2>
<p>
  <b>1. Maszyna Stanów i Kontrola Dostępności (State Machine & Lockdown)</b><br>
  Przechowuje stan rozgrywki w zmiennej <code>isStarted</code> oraz zarządza listą aktywnych graczy (<code>inGame</code>). Przed startem gry blokuje interakcje ekwipunkiem (<code>onInteract</code>), ruch graczy na poziomie klocków (<code>onMove</code>) oraz anuluje wszelkie obrażenia (<code>onDamage</code>).
</p>
<p>
  <b>2. Licznik Startowy i Obsługa Sesji (Lobby & Task Scheduler)</b><br>
  Po osiągnięciu progu 8 graczy w <code>onJoin</code> inicjalizuje opóźnione zadanie <code>BukkitRunnable</code> (20 sekund). W przypadku wyjścia gracza (<code>onQuit</code>) przed startem i spadku liczby graczy poniżej 8, zadanie jest natychmiastowo anulowane (<code>startGameTask.cancel()</code>). Osiągnięcie limitu 24 graczy blokuje możliwość wejścia na serwer (<code>onLogin</code> / kick).
</p>
<p>
  <b>3. Dynamiczne Kurczenie Świata (WorldBorder NMS/Bukkit)</b><br>
  Po upływie odliczania startowego zmniejsza promień granicy świata (<code>WorldBorder</code>) o 250 bloków w czasie 9600 ticków (8 minut). Metoda <code>onMove</code> wykrywa bliskość granicy (10 bloków) i wysyła powiadomienia na Actionbarze.
</p>
<p>
  <b>4. System Zestawów i Konsumpcja Główek (Kits & Consumables)</b><br>
  Reaguje na interakcję z zaklinaną książką w <code>onInteract</code>, przydzielając zestaw zbroi i przedmioty na podstawie szans procentowych (10%, 15%, 35%, 40%). Posiada wbudowaną logikę zjadania głowy gracza (<code>Material.SKULL_ITEM</code>), nadającą efekty Speed II, Regeneration IV oraz Absorption I.
</p>
<p>
  <b>5. Walidacja Zabójstw i Automatyczny Detektor Wygranej (Victory Check)</b><br>
  W metodach <code>onDeath</code> oraz <code>onQuit</code> (podczas gry) następuje usuwanie graczy z listy <code>inGame</code>, przydzielanie nagród za zabójstwo oraz weryfikacja warunku zwycięstwa. Gdy w <code>inGame</code> pozostanie tylko 1 gracz, wyzwalany jest zapis statystyk w <code>UserFile</code>, efekty końcowe oraz restart serwera.
</p>
<p>
  <b>6. Normowanie Komunikacji i Pakiety Ekranowe (Packets & Formatting)</b><br>
  Czyszczenie domyślnych komunikatów Bukkita (<code>setJoinMessage("")</code>, <code>setDeathMessage("")</code>) na rzecz natywnych pakietów NMS (<code>PacketPlayOutTitle</code>) oraz dostosowanych formatów czatu w zależności od uprawnień (<code>OP</code>, <code>SPECTATOR</code>, <code>GRACZ</code>).
</p>

<h2>Instalacja i Konfiguracja</h2>
<p>
  <b>1. Wymagania Wstępne</b><br>
  • Środowisko uruchomieniowe <b>Java 8 (JDK / JRE 1.8)</b>.<br>
  • Serwer Minecraft działający na silniku <b>Spigot 1.8.8</b> lub <b>(zalecane)</b> <a href="https://papermc.io/downloads/all" target="_blank">PaperSpigot 1.8.8</a>.
</p>
<p>
  <b>2. Budowanie i Przenoszenie Pliku</b><br>
  • Skompiluj projekt za pomocą komendy <code>mvn clean package</code> lub pobierz gotowy artefakt.<br>
  • Zlokalizuj wygenerowany plik <code>Filipesz-UHCMEETUP-0.0.1.jar</code> w katalogu <code>target/</code>.<br>
  • Przenieś plik <code>.jar</code> do katalogu <code>plugins/</code> na Twoim serwerze Minecraft.
</p>
<p>
  <b>3. Uruchomienie i Weryfikacja</b><br>
  Uruchom lub zrestartuj serwer Minecraft.
</p>