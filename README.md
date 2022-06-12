### TempleOSRS Plugin

A RuneLite plugin utilizing the [TempleOSRS API](https://templeosrs.com/api_doc.php). <br>
- - -

### Features

1. Fetch player's recorded gains by **username**. <br>
   ![image](https://user-images.githubusercontent.com/60162255/170362329-212ec277-db30-4e3b-b590-babed7ba2d16.png)

2. Fetch player via **menu-option** `Toggleable in config` <br>
   ![image](https://user-images.githubusercontent.com/60162255/172024020-49c2df01-ce6e-47a5-9571-c3dad2a03714.png)

3. Fetch clan by **TempleOSRS Clan-ID**. `Clan ID obtainable via TempleOSRS clan-page URL` <br>
   ![image](https://user-images.githubusercontent.com/60162255/170362348-d1b1774e-e918-4d8f-8e1e-9dc5173d21bb.png)

4. Fetch competition by **TempleOSRS Comp-ID**. `Competition ID obtainable via TempleOSRS competition-page
   URL`<br>
   ![image](https://user-images.githubusercontent.com/60162255/170364287-95dc2423-add6-4564-ba8e-ea04a201b9c5.png)

- - -

### Configuration

![image](https://user-images.githubusercontent.com/60162255/173243938-d41c0ff0-a44b-4fb2-8257-cb7b540b2764.png)

#### Menu Options

1. Toggle `autocomplete`
2. Toggle `Temple menu-option`

#### Clan Options

1. Toggle display of `clan-achievements`
2. Toggle display of `clan-members`
3. `Default clan` loaded on startup or search-icon double-click
4. `Verification key` for clan-sync
5. `Ignore specified ranks` during clan-sync
6. Toggle `only add members` during clan-sync rather than add/remove

#### Competition Options
1. `Default competition` loaded on startup or search-icon double-click
- - -

### To-Do / Potential Features

- [x] Implement ranks, clans and competitions tabs
- [x] Implement right-click menu option for fetching players
- [x] Add options to favorite clans/ competitions
- [x] Auto update local player's temple page on logout

- - -

### Bugs to fix/ Concerns

- [ ] 🐛 Searching for players without datapoints or those who have recently changed names and have yet to update their
  TempleOSRS profile return nothing.
