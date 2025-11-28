---
name: Feature request
about: Suggest an idea for this project
title: ''
labels: enhancement
type: Feature
assignees: LeeKaiXuan

---

## Description
A clear and concise description of what you want to happen.
E.g., Wish PSS-Gen supports Boolean type.

## Definition of the feature located in LRM
e.g., PSSv3.0/7.4 Booleans
**OR**
None; if the feature isn't located in LRM.

## Example Code
```pss
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
Please feel free to add any other context about the problem here.
