---
name: Bug report
about: Create a report to help us improve
title: ''
labels: bug
type: Bug
assignees: LeeKaiXuan

---

## Description
A clear and concise description of what the bug is.

## Scenario Code
```pss
action example {
    rand int val;
    constraint {
        val in [1];
    }
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

## Error Message from PSSGen or JAVA (Optional)
```
PSS-ERROR: ...
```
**OR**
None; if no error message exists.

## Additional Context (Optional)
Please feel free to add any other context about the problem here.
