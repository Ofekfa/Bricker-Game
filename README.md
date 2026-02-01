# Bricker Game

A Breakout-style game implemented in Java (uses the `danogl` game framework). The project demonstrates object-oriented design and common game-engine patterns through a modular implementation of bricks, paddles, balls and pluggable collision behaviors.

## Quick start

- Open the project in `IntelliJ IDEA 2025.2.3` on macOS.
- Ensure Java (11+) is configured and the `danogl` dependency is available on the classpath.
- Run the main class: `bricker.main.BrickerGameManager`.

## Repository layout

- `assets/` — images and sounds used by the game.
- `src/` — Java sources:
  - `bricker.main.BrickerGameManager` — application entrypoint and game setup.
  - `bricker.gameobjects.*` — game object implementations (`Ball`, `Brick`, `Paddle`, `ExtraPaddle`, `FallingHeart`, `LivesUI`, `Puck`).
  - `bricker.brick_strategies.*` — collision strategy implementation classes (`CollisionStrategy`, `BasicCollisionStrategy`, `ExplodingBrickStrategy`, `CombinedCollisionStrategy`, `StrategyFactory`, etc.).

## What the project demonstrates 

- Encapsulation
  - Game objects expose behavior through methods while hiding internal state (fields are `private` / `final` where appropriate).
- Inheritance & Polymorphism
  - Common behavior is implemented in base classes (game engine types). Custom behaviors are provided by subclassing or implementing interfaces from the engine.
- Interfaces & Strategy Pattern
  - `CollisionStrategy` defines a contract for brick-collision behavior.
  - Multiple concrete strategies (`BasicCollisionStrategy`, `ExplodingBrickStrategy`, `ExtraPaddleStrategy`, `ReturnLifeStrategy`) implement this contract and can be swapped at runtime.
- Composition
  - `CombinedCollisionStrategy` composes multiple strategies to build complex behavior from simple pieces.
- Single Responsibility & Separation of Concerns
  - Rendering/physics are handled by the engine; application code focuses on game rules and object construction.
- Factory Pattern
  - `StrategyFactory` centralizes creation of strategy instances based on configuration.

## Key classes

- `bricker.main.BrickerGameManager`
  - Sets up the scene, loads assets and creates game objects and strategies.
- `bricker.gameobjects.Brick`
  - Represents breakable blocks; delegates collision handling to a `CollisionStrategy`.
- `bricker.brick_strategies.CollisionStrategy` (interface)
  - Defines `onCollision(GameObject objA, GameObject objB)` for collision response.
- `bricker.brick_strategies.BasicCollisionStrategy`
  - Removes the brick and decrements the bricks counter.
- `bricker.brick_strategies.ExplodingBrickStrategy`
  - (Composite behavior) removes the brick and spawns an explosion of small bricks/particles; interacts with `GameObjectCollection` and `Counter`.
- `bricker.brick_strategies.CombinedCollisionStrategy`
  - Runs several strategies in sequence to combine effects.

## Coding skills demonstrated

- Clean package organization and naming conventions.
- Use of final/immutable fields where appropriate.
- Dependency injection: passing `GameObjectCollection` and `Counter` to strategies rather than static access.
- Small, composable classes that are easy to test.
- Javadoc comments and brief inline documentation.
- Resource management: loading and using assets from `assets/`.
- Event-driven programming: collision callbacks and game-loop updates.

## Extending the project

- Add new brick behaviors by implementing `CollisionStrategy` and registering via `StrategyFactory`.
- Add new power-ups as `GameObject` subclasses and spawn them from strategies or bricks.
- Unit-test behavior by constructing minimal `GameObjectCollection` mocks and `Counter` instances.

## Notes

- This project relies on the `danogl` game framework (imports like `danogl.GameObject`).
- Keep game logic separate from rendering/physics for easier maintenance and testing.
