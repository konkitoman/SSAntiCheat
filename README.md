# SSAntiCheat

This project is not that usabile.

## Anti XRay

An anti XRay module, that is probably very resource heavy!

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
