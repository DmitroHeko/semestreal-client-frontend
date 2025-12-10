Collaborative Study Platform  
Semestrálny projekt – FEI STU  
Predmet: **Tvorba softvéru v IKT**

---
#  Heiko Dmytro
#  Stručný popis projektu a cieľov aplikácie  
Collaborative Study Platform je desktopová tímová aplikácia pre študentov, umožňujúca správu študijných skupín, úloh, materiálov, komunikáciu v reálnom čase a vizualizáciu štatistík.  
Aplikácia rieši problémy neprehľadnej tímovej spolupráce, chýbajúcej komunikácie a nejednotného zdieľania zdrojov.

Funkcie aplikácie:  
- Registrácia a prihlásenie používateľov  
- Správa profilu (zmena mena, e‑mailu, hesla)  
- Rozdelenie rolí **Admin / User**  
- Vytváranie a správa študijných skupín  
- Zadávanie úloh s termínmi  
- Real‑time chat (WebSocket)  
- Zdieľanie materiálov (súbory / odkazy)  
- WebSocket Activity Log  
- Vizualizácia štatistík



---

#  Architektúra systému  
## Frontend – JavaFX  
- GUI (Skupiny, Úlohy, Materiály, Štatistiky, Profil)  
- WebSocket klient  
- REST API klient  

## Backend – Spring Boot  
- REST API  
- WebSocket server  
- Autentifikácia (bcrypt)  
- Rolový systém  

## Databáza – SQLite  




---

# Databázový model (ER diagram)  
Tabuľky: users, groups, group_members, tasks, chat_messages, resources  
Vzťahy: User–Group (N:N), Group–Tasks (1:N), Group–Resources (1:N), Group–Messages (1:N)

![ER Diagram](https://github.com/user-attachments/assets/cab6f773-c970-42ce-a448-18f8171bd7c7)


---

#  Dokumentácia REST API a WebSocket endpointov  
### Autentifikácia  
POST `/api/auth/register`  
POST `/api/auth/login`

### Groups  
POST `/api/groups`  
GET `/api/groups`  
PUT `/api/groups/{id}`  
DELETE `/api/groups/{id}`  

### Tasks  
POST `/api/tasks`  
GET `/api/tasks/group/{id}`  
PUT `/api/tasks/{id}/status`  

### Resources  
POST `/api/resources`  
GET `/api/resources/group/{id}`  

### Chat  
POST `/api/chat/send`  
GET `/api/chat/group/{id}`  

### WebSocket  
`/topic/groups/{id}/chat`  
`/topic/groups/{id}/tasks`  
`/topic/groups/{id}/resources`

📸 *SEM VLOŽ WEBSOCKET DIAGRAM*  
`images/ws.png`

---

# Ukážky používateľského rozhrania  

## 1. Registrácia

![Registrácia](https://github.com/user-attachments/assets/d479b915-999d-41d6-a050-b1f72ce3eabf)


## 2. Správaprofilov 
![Správa profilov](https://github.com/user-attachments/assets/ddcf9c1a-5067-4836-9eff-4812d869a646)

## 3.  pridávaniečlenov
![Pridávanie členov](https://github.com/user-attachments/assets/a408afac-ded3-43e0-8967-38b60507373b)


## 3. Vytváranie a správa študijných skupín
![Skupiny](https://github.com/user-attachments/assets/0b67566c-13f0-43ef-9974-be955176e80a)

## 3. Riadenie úloh v skupine
![Úlohy](https://github.com/user-attachments/assets/0b90845b-44d8-44f0-9987-e40ca46a22fd)


## 3. Termíny a upozornenia
![Termíny](https://github.com/user-attachments/assets/e274aa73-9df7-4de8-959a-7d2682e656ff)


## 4. Zdieľanie študijných materiálov
![Materiály](https://github.com/user-attachments/assets/5913b3cf-9900-490d-abd8-41b0eda795a4)


## 5. Real-time notifikácie (WebSocket)
![Notifikácie](https://github.com/user-attachments/assets/5ad51184-e054-4dd5-aea1-7bf341b99c36)


## 6. Diskusia / správy (voliteľné rozšírenie)
![Chat](https://github.com/user-attachments/assets/4ebb3259-ae9a-46eb-800b-efba2473fc33)


## 7. Vizualizácia štatistík
![Štatistiky](https://github.com/user-attachments/assets/d948726b-8934-47c3-ba67-91db7a453aa7)


## 8. Bezpečné spracovanie dát
![Bezpečnosť](https://github.com/user-attachments/assets/ff4d4a3e-0da5-459e-8942-703430f10836)




---

#  Popis výziev a riešení  
**Validácia vstupov:** riešená cez JavaFX + Bean Validation  
**Autentifikácia:** bcrypt + validácia hesla  
**Serializácia dátumov:** Instant → String  
**WebSocket reconnect:** vlastný listener  
**JavaFX routing:** SceneManager  
**CORS a JSON problémy:** nastavenie v Spring Boot  

---

#  Zhodnotenie práce s AI  
AI pomohlo pri:  
- návrhu API  
- generovaní DTO  
- ladení chýb Jackson/Lombok  
- návrhu štruktúry projektu  
- písaní README

Manuálne bolo potrebné:  
- doladiť WebSocket logiku  
- optimalizovať SQL dotazy  
- spraviť UI routing  
- vyriešiť bezpečnosť a validáciu

---

#  Spustenie projektu  

## Backend
```
semestreal-backend_1


```

## Frontend
```
semestreal-client-frontend
```

---

