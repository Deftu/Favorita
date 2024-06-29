# Favorita
A simple Minecraft mod which allows you to favorite your slots.

---

[![Crowdin Badge](https://wsrv.nl/?url=https%3A%2F%2Fbadges.crowdin.net%2Ffavorita%2Flocalized.svg&n=-1&maxage=1d)](https://crowdin.com/project/favorita)

[![Discord Badge](https://raw.githubusercontent.com/intergrav/devins-badges/v2/assets/cozy/social/discord-singular_64h.png)](https://s.deftu.dev/discord)
[![Ko-Fi Badge](https://raw.githubusercontent.com/intergrav/devins-badges/v2/assets/cozy/donate/kofi-singular_64h.png)](https://s.deftu.dev/kofi)

---

## For players

### How do I use it?

The mod adds a new keybinding called "Favorite Slot" which is set to `F` by default.  
When you press it while hovering over an item in your inventory, it will mark that slot as a favorite.  
Or, if you're not in an inventory screen, it will mark the hotbar slot you currently have selected as favorited.

### What does the mod block?

If the (hotbar) slot is favorited, you can't:
- Move the item within it
- Drop the item
- Wipe that slot using the bin slot
- Quick move items into or out of that slot
- Swap an item into or out of that slot

### Where will the mod block these actions?

- In the player inventory
- In the hotbar in any container
- In your offhand slot
- In your armor slots

---

## For developers

### How do I add compatibility for it with my mod?

An API is exposed via `dev.deftu.favorita.api.FavoritedSlots` which allows you to check if a slot is favorited.  
Functions are provided to check if a slot is favorited, mark a slot as favorited, and unmark a slot as favorited. Both for the current identifier, if one is present, and for a specific identifier.

### What is an "identifier"?

Identifiers are strings used to determine the current environment the player is in. The value of an identifier is a constant `Singleplayer` when the player is in singleplayer, and the address of their current server entry when they're playing in multiplayer.

---

[![BisectHosting](https://www.bisecthosting.com/partners/custom-banners/8fb6621b-811a-473b-9087-c8c42b50e74c.png)](https://s.deftu.dev/bisect)

---

**This project is licensed under [LGPL-3.0][lgpl]**\
**&copy; 2024 Deftu**

[lgpl]: https://www.gnu.org/licenses/lgpl-3.0.en.html
