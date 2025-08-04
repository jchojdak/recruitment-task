package com.jchojdak.recruitmenttask.dto;

public record BranchDto(
        String name,
        CommitDto commit
) {}