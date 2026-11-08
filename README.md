# Fortune Touch Switch

Put **Silk Touch** and **Fortune** on the same tool, and switch between them by right-clicking the air.
Only one of them works at a time; the other one is kept on the tool and comes back when you switch.

## Versions

| Loader   | Minecraft                                              | Needs      |
|----------|--------------------------------------------------------|------------|
| Fabric   | 1.21.1 – 1.21.11, 26.1 – 26.3                          | Fabric API |
| NeoForge | 1.21.1, 1.21.3 – 1.21.5, 1.21.8, 1.21.10, 1.21.11, 26.1.2 – 26.3 |            |

## Layout

- `fabric/<mc>/`, `neoforge/<mc>/`: one small Gradle project per Minecraft version
- `shared/`: the code, split by the Minecraft version range it compiles against, plus the mod metadata templates
- `VERSION`: the mod version for every build

## Build

```sh
cd fabric
./gradlew -p 26.3 build
```

The jar is written to `fabric/26.3/build/libs/`. GitHub Actions builds every version on each push and attaches them to releases.

---

# Fortune Touch Switch（日本語）

シルクタッチと幸運を1つの道具に両方付けられるようにして、何もないところを右クリックで切り替えられるようにします。
効くのはどちらか片方だけで、もう片方は道具にしまわれていて、切り替えると戻ってきます。

Fabric版には Fabric API が必要です。

## License

MIT © Mya
