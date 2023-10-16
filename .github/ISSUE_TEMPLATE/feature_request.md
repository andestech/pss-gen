---
name: Feature request
about: Suggest an idea for this project
title: ''
labels: enhancement
assignees: LeeKaiXuan

---

## Description
A clear and concise description of what you want to happen.
E.g., Wish PSS-Gen supports Boolean type.

## Definition of the feature located in LRM
e.g., PSSv2.1/7.4 Booleans
**OR**
None; if the feature isn't located in LRM.

## Example Code
```sv
action test {
    bool bVal = true;
    exec body ASM =
"""
bVal = {{bVal}}
""";
}
```

## Expected Result or Behaviour
```
bVal = 1;
```
**OR**
You can describe expected behaviour here.

## Additional Context (Optional)
Add any context you need in this section.