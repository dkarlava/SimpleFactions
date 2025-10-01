# CHANGELOG
## From `alpha-v1.0.1` to `alpha-v1.1.0`
  ### Bugs
  - Fixed an issue where players could claim land from the safezone/ warzone
  - Removed characters from the faction map that caused the width of the rows to be inconsistent

  ### Features
  - Added `/f map on` that will auto display the faction map every time the player moves a chunk
    - `/f map off` can be used to cancel the automatic map
    - `/f map off` will be automatically called when the player leaves the server
  - Added the ability for one faction to overclaim the land of another faction if the factions total power is less than the total land claimed
