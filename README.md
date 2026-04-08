# task-tracker-cli

A Java CLI app to manage tasks with local persistence in `tasks.json`.

## Requirements

- Java 23
- Maven

## Run

```bash
mvn -q -DskipTests package
java -cp target/classes com.murilodcosta.tasktrackercli.Application add "New Task"
```

## Commands

```text
task-cli add "description"
task-cli update <id> "new description"
task-cli delete <id>
task-cli mark-in-progress <id>
task-cli mark-done <id>
task-cli list
task-cli list todo|in-progress|done
```

## Persistence

- `tasks.json` is created automatically in the current directory.
- Mutating operations persist automatically (`add`, `update`, `delete`, `mark-*`).
- `status` is stored as: `todo`, `in-progress`, `done`.

Example saved item:

```json
{
  "id": "1",
  "description": "New Task",
  "status": "todo",
  "createdAt": "2026-04-07T20:00:00",
  "updatedAt": "2026-04-07T20:00:00"
}
```

## Tests

```bash
mvn clean test
```
