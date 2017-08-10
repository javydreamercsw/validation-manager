/*Default Workflows*/
/*Approval*/
INSERT INTO `workflow` (`id`, `workflow_name`) VALUES (1, 'worflow.approval');

INSERT INTO `workflow_step` (`id`, `workflow`, `step_name`) VALUES (1, 1, 'general.start');
INSERT INTO `workflow_step` (`id`, `workflow`, `step_name`) VALUES (2, 1, 'general.approved');
INSERT INTO `workflow_step` (`id`, `workflow`, `step_name`) VALUES (3, 1, 'general.rejected');

INSERT INTO `step_transitions_to_step` (`source_step`, `source_step_workflow`, `target_step`, `target_step_workflow`, `transition_name`) VALUES (1, 1, 2, 1, 'general.approve');
INSERT INTO `step_transitions_to_step` (`source_step`, `source_step_workflow`, `target_step`, `target_step_workflow`, `transition_name`) VALUES (1, 1, 3, 1, 'general.reject');
