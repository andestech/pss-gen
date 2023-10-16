---
name: Bug report
about: Create a report to help us improve
title: ''
labels: bug
assignees: LeeKaiXuan

---

## Description
A clear and concise description of what the bug is.

## Scenario Code
```sv
action example {
    rand int in [1] val;
    exec body ASM =
"""
val = {{val}}
""";
}
```

## Expected Output or Behaviour
```
val = 1
```
**OR**
You can describe expected behaviour here.

## Error Message from PSSGen or JAVA
```
PSS-ERROR: ...
```
**OR**
None; if no error message exists.

## Additional Context (Optional)
Add any other context about the problem here.