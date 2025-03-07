---
layout: page
title: User Guide
---

## Configurations

Refer to the [Configuration Guide](configurations.html).

## Commands

- `/lrstart [player | center] [name]` - start a large raid at player location or given center
- `/lrstop [player]` - stop a large raid in the vicinity (or the player's vicinity)
- `/lrcenters [add | remove | show | hide] [name]` - manage artificial villages
- `/lrgive <player> [amount]` - give player summoning items
- `/lrreload` - reload plugin configurations

## Placeholders

> Note: You will require [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) to use these placeholders!

- `%largeraids_in_range%` - **true** if player is in vicinity of a large raid, **false** otherwise
- `%largeraids_wave%` - current wave number
- `%largeraids_total_waves%` - number of waves in total
- `%largeraids_remaining_raiders%` - number of remaining raiders
- `%largeraids_omen_level%` - accumulative bad omen level
- `%largeraids_player_kills%` - raiders killed by player

## Permissions

- `largeraids.admin` - user able to perform plugin commands

## Limitations

- **Mobs** - raiders are limited to entity type raiders
