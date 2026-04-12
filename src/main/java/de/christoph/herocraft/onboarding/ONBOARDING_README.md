# HeroCraft Onboarding System

## Übersicht

Das Onboarding-System ist ein Tutorial-erlebnis für neue Spieler im HeroCraft Plugin. Es leitet Spieler Schritt für Schritt durch die wichtigsten Aufgaben des Spiels.

## Features

✅ **Schrittweise Anleitung**: Spieler werden durch klar definierte Aufgaben geführt
✅ **Fortschritts-Speicherung**: Der aktuelle Onboarding-Schritt wird in der Config gespeichert
✅ **Persistenz**: Wenn ein Spieler den Server verlässt und zurückkehrt, wird der Fortschritt beibehalten
✅ **Automatischer Start**: Neuen Spielern (First Join) wird das Onboarding automatisch gestartet
✅ **Erinnerungsnachrichten**: Bei Todesfällen erhalten Spieler Erinnerungsnachrichten
✅ **Schultz-Modus**: Während des Onboardings sind andere Aktionen blockiert

## Onboarding-Schritte

### Aktuell implementierte Schritte:
1. **CREATE_LAND** (ID: 0) - Spieler sollen ihr erstes Land erstellen
2. **COMPLETED** (ID: 1) - Onboarding abgeschlossen

### Zukünftige Schritte (vorbereitet):
- Ersten Bewohner einziehen lassen
- Zum Markt gehen und Startitems kaufen
- Erste Challenge absolvieren
- Job annehmen
- Navigator benutzen

## Wie es funktioniert

### 1. Automatischer Start für neue Spieler
```java
// Beim PlayerJoinEvent:
if (!player.hasPlayedBefore()) {
    onBoardingManager.startOnBoarding(player);
}
```

### 2. Manueller Start
Spieler können das Onboarding jederzeit mit folgendem Command starten:
```
/startonboarding
```

### 3. Speicherung und Laden
- **onPlayerQuit**: Der aktuelle Schritt wird in der Config gespeichert
- **onPlayerJoin**: Der Schritt wird aus der Config geladen und angezeigt
- **Abschluss**: Wenn das Onboarding abgeschlossen wird, wird ein Flag in der Config gesetzt

### 4. Beschränkungen während des Onboardings
Spieler können während des Onboardings NICHT:
- ❌ Blöcke zerstören oder platzieren
- ❌ Mit anderen Spielern oder Mobs kämpfen (PVP/PVE)
- ❌ Mit Truhen, Türen, Hebeln interagieren
- ❌ Die meisten Commands nutzen (nur whitelisted Commands erlaubt)

### 5. Erlaubte Aktionen
- ✅ Land erstellen (durch Platzierung des Land-Erstellungs-Items)
- ✅ Bestimmte Commands: /job, /land, /createland, /befehle, /spawn, /help, etc.
- ✅ Normales Bewegen und Chatten

## Klassen-Struktur

### `OnBoarding.java`
Repräsentiert ein einzelnes Onboarding für einen Spieler.
- Speichert den aktuellen Schritt
- Verwaltet die Config-Speicherung
- Sendet Nachrichten an den Spieler

### `OnBoardingManager.java`
Zentrale Manager-Klasse für alle Onboardings.
- Verwaltet aktive Onboardings in einer ArrayList
- Lädt/speichert Onboardings aus der Config
- Prüft den Status von Spielern

### `OnBoardingListener.java`
Event-Listener für wichtige Game Events.
- **onFurniturePlaced**: Erkennt Land-Erstellung und beendet Schritt 1
- **onPlayerJoin**: Startet Onboarding für neue Spieler, lädt bestehende
- **onPlayerDeath**: Sendet Erinnerungsnachrichten
- **onPlayerQuit**: Speichert den Fortschritt

### `OnBoardingProtectionListener.java`
Blockiert unerwünschte Aktionen während des Onboardings.
- Blockiert Block-Interaktionen
- Blockiert Kampfaktionen
- Blockiert Command-Ausführung

### `OnBoardingStep.java`
Enum der verfügbaren Onboarding-Schritte.

### `StartOnBoardingCommand.java`
Command-Executor für `/startonboarding`.

## Config-Format

Das Onboarding wird in der `config.yml` speichert:

```yaml
ON_BOARDINGS:
  - "player_uuid_ON_BOARDING"
  - "player_uuid2_ON_BOARDING"

player_uuid_ON_BOARDING: 0  # Aktueller Schritt ID

player_uuid_ON_BOARDING_COMPLETED: true  # Onboarding abgeschlossen
```

## Wie man Schritte hinzufügt

### 1. OnBoardingStep Enum erweitern
```java
public enum OnBoardingStep {
    CREATE_LAND(0),
    RECRUIT_VILLAGER(1),      // Neuer Schritt
    COMPLETED(2);
    
    // ... Rest bleibt gleich
}
```

### 2. Nachricht hinzufügen
```java
// In OnBoarding.sendCurrentStepMessage():
case RECRUIT_VILLAGER:
    player.sendMessage("§e§lOnboarding §7§l| §7Nächster Schritt: Rekrutiere deinen ersten Bewohner!");
    break;
```

### 3. Event-Listener erstellen
```java
// In OnBoardingListener.java:
@EventHandler
public void onVillagerRecruited(VillagerRecruitEvent event) {
    Player player = event.getPlayer();
    OnBoarding onBoarding = getOnBoardingForPlayer(player);
    
    if (onBoarding != null && onBoarding.getCurrentStep() == OnBoardingStep.RECRUIT_VILLAGER) {
        onBoarding.setCurrentStep(OnBoardingStep.NEXT_STEP);
    }
}
```

## Verwaltungs-Commands

```bash
/startonboarding          # Startedas Onboarding
/land                     # Land-GUI öffnen
/createland              # Landerstellen-GUI öffnen
/status                  # Zeigt Onboarding-Status (optional)
```

## Debugging

Zur Überprüfung des Onboarding-Status:
1. Prüfe die `config.yml` auf Einträge mit `_ON_BOARDING`
2. Nutze `/status` oder log-Nachrichten zur Diagnose
3. Setze einen Breakpoint in `OnBoardingListener` zum Debuggen

## Zukünftige Verbesserungen

- [ ] GUI für Onboarding-Fortschritt
- [ ] Checkpoint-System für längere Tutorials
- [ ] Belohnungen für Onboarding-Abschluss
- [ ] Optionale Onboarding-Deaktivierung
- [ ] Onboarding-Statistiken und Tracking
