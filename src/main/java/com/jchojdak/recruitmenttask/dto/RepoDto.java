package com.jchojdak.recruitmenttask.dto;

import java.util.List;

public record RepoDto(
        String name,
        boolean fork,
        OwnerDto owner,
        List<BranchDto> branches
) {}
