## SQL-скрипти

---

## Структура бази даних

- **База даних:** `course`
- **Таблиця:** `course`

```sql
CREATE TABLE course (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    teacher_id BINARY(16),
    group_id BINARY(16)
);
```

UUID зберігаються у вигляді `BINARY(16)` для компактності.
