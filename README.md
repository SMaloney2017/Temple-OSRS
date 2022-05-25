### TempleOSRS Plugin

> A RuneLite plugin utilizing the [TempleOSRS API](https://templeosrs.com/api_doc.php). <br>

![1653510844](https://user-images.githubusercontent.com/60162255/170362329-212ec277-db30-4e3b-b590-babed7ba2d16.png)
![1653510856](https://user-images.githubusercontent.com/60162255/170362348-d1b1774e-e918-4d8f-8e1e-9dc5173d21bb.png)
![1653511669](https://user-images.githubusercontent.com/60162255/170364287-95dc2423-add6-4564-ba8e-ea04a201b9c5.png)

### To-Do / Potential Features

> 1. ✅ **COMPLETED:** ~~Implement functionality for sorting each activity panel by name, gain, rank, or EHP.~~
> 2. ✅ **COMPLETED:** ~~Right-click menu option to lookup player.~~
> 3. ⚙️ **IN PROGRESS:** Add support for other TempleOSRS API Endpoints. (Competitions, Groups)
>     * ✅ ~~Implemented class skeleton for Groups/ Competitions~~
>     * ✅ ~~Design *Clans tab* (Fetch clan, Overview, leaders, members, member lookup, members sync)~~
>     * 🔧 Design *Competitions tab* ~~(Fetch competition, competition rankings, participant lookup,~~ add to watchlist)
> 4. ✅ **COMPLETED:** ~~Save a Snapshot of the current view.~~
> 5. 💡 Add toggle to hide skills/bosses without gains.
> 6. ⚠️ Add rigorous comments.

### Bugs to fix/ Concerns

> 1. ⚠️~~Thread is locked up while fetching player data.~~
>    * **FIXED:** Created new thread to handle data fetching/ panel rebuilding
> 2. 🐛 Searching for players who have recently changed names and have yet to update their TempleOSRS profile return nothing.
> 3. 🐛 Players without data-points on temple return nothing.
>    * **POTENTIAL FIX:** Send request to update player before fetching user.
> 4. 📓 Fetching clans with larger member-counts causes a short delay while changing tabs.
>    * **COMPROMISE:** ~~Add togglable display of clan-members.~~
