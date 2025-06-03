# SSAntiCheat

This project is not that usabile.
This project is an archive!

## Anti XRay

An anti XRay module, that is probably very resource heavy!

When a chunk is sent, all the blocks will be checked and replaced with a shadow if is hidden,
and after is sent the chunk will be reverted back, this can be really expensive with multiple players, but for me is faster then expected.

And if the shadow_mode is solid the chunks will be very nicely compressed and will use less bandwidth then normal.

But if the shadow_mode is random this will use a lot of bandwidth.

### Commands

- `/ssanticheat enable xray`
- `/ssanticheat disable xray`
- `/ssanticheat xray set_mode light`

  If the block light level is 0 the block will be hidden.

- `/ssanticheat xray set_mode visible`

  If the block cannot be seen will be hidden.

- `/ssanticheat xray set_shadow random [<blocks>]`

  The block chosen to hide with will be randomly selected.
  If the `[<blocks>]` is empty the default minecraft ores will be added to the list.
  The blocks needs to be opaque!

- `/ssanticheat xray set_shadow solid <block>`

  Any hidden block will be replaced with the specified block.
  The block needs to be opaque!

### Images

![](./.github/images/spec1_n1.png)

  Under the world with night vision.

![](./.github/images/spec1_mlight_srandom_n1.png)

  Under the world with night vision and anti XRay.
  - `/ssanticheat enable xray`
  - `/ssanticheat xray set_mode light`
  - `/ssanticheat xray set_shadow random`

![](./.github/images/wcave1_mlight_srandom_n0.png)

  - `/ssanticheat enable xray`
  - `/ssanticheat xray set_mode light`
  - `/ssanticheat xray set_shadow random`

![](./.github/images/wcave1_mlight_srandom_n1.png)

  With night vision.
  - `/ssanticheat enable xray`
  - `/ssanticheat xray set_mode light`
  - `/ssanticheat xray set_shadow random`

![](./.github/images/wcave1_mlight_ssolid-black-concrete_n0.png)

  - `/ssanticheat enable xray`
  - `/ssanticheat xray set_mode light`
  - `/ssanticheat xray set_shadow solid minecraft:black_concrete`

![](./.github/images/wcave1_mlight_ssolid-black-concrete_n1.png)

  With night vision.
  - `/ssanticheat enable xray`
  - `/ssanticheat xray set_mode light`
  - `/ssanticheat xray set_shadow solid minecraft:black_concrete`

### Known Problems

- If a block modifies the sky light the blocks that are more 15 block away will not be updated, this is a sync issue.

  You can brake or place a block to refresh a zone.
