package com.oversea.task.mapper;

import com.oversea.task.domain.JobSequence;
public interface JobSequenceDAO {
	public void addJobSequence(JobSequence jobSequence);
	public void updateJobSequenceByDynamic(JobSequence jobSequence);
	public JobSequence getJobSequenceById(Long id);
	public int countJobSequenceById(Long id);
	public JobSequence getJobSequenceByName(String name);
	public int updateJobSequenceById(Long id,Long fromStart,Long toStart);

}