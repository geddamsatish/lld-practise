# LLD Practise

Java 21 + Maven project for Low Level Design practice.

## Layout

```
src/main/java/com/example/lld/
    common/          shared helpers / interfaces
    <problem>/       one package per problem (e.g. parkinglot, elevator, bookmyshow)
src/test/java/com/example/lld/
    <problem>/       tests mirroring the main packages
```

## Commands

```bash
mvn clean compile   # compile
mvn test            # run tests
mvn clean verify    # full build
```

## Workflow for a new problem

1. Create `src/main/java/com/example/lld/<problem>/`.
2. Write requirements in a comment or `NOTES.md` inside that package.
3. Model entities, then implement with the patterns the problem needs.
4. Add tests under `src/test/java/com/example/lld/<problem>/`.
