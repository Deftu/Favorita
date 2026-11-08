# Favorita

Lock your favorite inventory slots so you never drop or move them by accident.

---

[![Crowdin Badge](https://wsrv.nl/?url=https%3A%2F%2Fbadges.crowdin.net%2Ffavorita%2Flocalized.svg&n=-1&maxage=1d)](https://crowdin.com/project/favorita)

[![Discord Badge](https://raw.githubusercontent.com/intergrav/devins-badges/v2/assets/cozy/social/discord-singular_64h.png)](https://s.deftu.dev/discord)
[![Ko-Fi Badge](https://raw.githubusercontent.com/intergrav/devins-badges/v2/assets/cozy/donate/kofi-singular_64h.png)](https://s.deftu.dev/kofi)
[![Modrinth Badge](https://raw.githubusercontent.com/intergrav/devins-badges/v2/assets/cozy/available/modrinth_64h.png)](https://modrinth.com/mod/favorita)

---

## What it does
Press the favorite slot key (`B` by default) while hovering a slot in your inventory to lock it. Press it outside an inventory to lock the hotbar slot you're holding. Press it again to unlock.

A locked slot stops you from dropping its item, picking it up, putting items into it, moving it with shift-click, number keys or the offhand key, and gathering from it with a double-click. Shift-clicked and dragged stacks skip it and go to the next free slot. It works on your hotbar, main inventory, armor and offhand, in any container, in survival and creative.

Each world and server keeps its own locked slots.

Favorita needs [OneConfig][oneconfig], and its settings are in the OneConfig menu. There you can change the key, stop it from unlocking slots outside your inventory, the lock's color, when hotbar locks show, and how you're told that a slot is locked. Run `/favorita` to open them, or `/favorita clear` to unlock everything in the current world or server.

---

## For developers
`dev.deftu.favorita.api.FavoritedSlots` lets your mod check and change locked slots, either for the current world or server or for a place you name.

A place is identified by a server's address, or by `world:` and a singleplayer world's save folder name. Slots are player inventory indices: 0 to 8 for the hotbar, 9 to 35 for the main inventory, 36 to 39 for armor and 40 for the offhand.

Locks are saved in `config/Favorita/favorites.json`.

---

[![BisectHosting](https://www.bisecthosting.com/partners/custom-banners/8fb6621b-811a-473b-9087-c8c42b50e74c.png)](https://s.deftu.dev/bisect)

---

**This project is licensed under [AGPL-3.0-or-later][agpl]**\
**&copy; 2024 Deftu**

[oneconfig]: https://polyfrost.org/projects/oneconfig
[agpl]: https://www.gnu.org/licenses/agpl-3.0.en.html
