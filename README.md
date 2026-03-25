# 8man - Strategischer Expertenrat-Simulator

Multi-Agenten-System basierend auf der **Achter-Mann-Regel** (Tenth Man Rule) zur strukturierten Analyse komplexer Fragestellungen.

## Konzept

7 Konsens-Agenten erarbeiten eine fundierte Mehrheitsmeinung, während ein 8. Agent (der "Achte Mann") als systematischer Kritiker alle Annahmen hinterfragt. Das System durchläuft 4 Phasen:

1. **Analyse** - Jeder Agent liefert seine individuelle Einschätzung
2. **Konsensbildung** - Identifikation von Gemeinsamkeiten und Unterschieden
3. **Dissens** - Der Achte Mann formuliert seine strukturierte Gegenposition
4. **Synthese** - Finale Bewertung mit Pro/Contra, Risiken und Empfehlungen

## Voraussetzungen

- Java 8+ (getestet mit Java 21)
- OpenAI-kompatibler API-Key

## Build & Start

```bash
# Build
chmod +x build.sh
./build.sh

# Environment konfigurieren
export EIGHTMAN_API_KEY="your-api-key"

# Optionale Konfiguration
export EIGHTMAN_API_BASE_URL="https://api.openai.com/v1"
export EIGHTMAN_DEFAULT_MODEL="gpt-4o"
export EIGHTMAN_PORT="8080"
export EIGHTMAN_MAX_PARALLEL="3"

# Starten
java -jar build/jar/8man.jar
```

Dann im Browser `http://localhost:8080` öffnen.

## Konfiguration

| Variable | Beschreibung | Default |
|---|---|---|
| `EIGHTMAN_API_KEY` | API-Key (erforderlich) | - |
| `EIGHTMAN_API_BASE_URL` | API-Basis-URL | `https://api.openai.com/v1` |
| `EIGHTMAN_DEFAULT_MODEL` | Standard-Modell | `gpt-4o` |
| `EIGHTMAN_SYNTHESIS_MODEL` | Modell für Synthese | wie Default |
| `EIGHTMAN_MAX_TOKENS` | Max Tokens pro Antwort | `2000` |
| `EIGHTMAN_TEMPERATURE` | Temperatur | `0.7` |
| `EIGHTMAN_PORT` | Server-Port | `8080` |
| `EIGHTMAN_MAX_PARALLEL` | Max parallele API-Aufrufe | `3` |

## Technologie

- **Backend:** Java 8 mit `com.sun.net.httpserver.HttpServer` (keine externen Abhängigkeiten)
- **Frontend:** Vanilla HTML/CSS/JavaScript
- **API:** REST, OpenAI-kompatibel (Chat Completions)

## API-Endpunkte

| Methode | Pfad | Beschreibung |
|---|---|---|
| `POST` | `/api/cases` | Fall anlegen |
| `GET` | `/api/cases/{id}` | Fall abrufen |
| `POST` | `/api/cases/{id}/consult` | Beratung starten |
| `GET` | `/api/cases/{id}/status` | Status/Ergebnis abrufen |
