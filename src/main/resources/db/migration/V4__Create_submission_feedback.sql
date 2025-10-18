/*
 * Student Assignments: This is the 'instance' of an assignment,
 * given to one specific student. This is how you assign to *one or more* students.
 * The 'status' and 'due_date' live here.
*/
CREATE TABLE student_assignments (
     id UUID PRIMARY KEY,
     assignment_id UUID NOT NULL, -- The template
     student_id UUID NOT NULL,
     mentor_id UUID NOT NULL, -- Denormalized for easy queries
     status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'IN_PROGRESS', 'SUBMITTED', 'COMPLETED')),
     assigned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
     due_date TIMESTAMPTZ, -- Optional due date from your requirements

     CONSTRAINT fk_sa_assignment FOREIGN KEY (assignment_id) REFERENCES assignments(id) ON DELETE CASCADE,
     CONSTRAINT fk_sa_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
     CONSTRAINT fk_sa_mentor FOREIGN KEY (mentor_id) REFERENCES users(id) ON DELETE CASCADE,
     CONSTRAINT uq_student_assignment UNIQUE (assignment_id, student_id)
);

/*
 * Submissions: A student's work for a *specific problem* on an assignment.
 * Includes 'write_up' and 'version' as required.
*/
CREATE TABLE submissions (
     id UUID PRIMARY KEY,
     student_assignment_id UUID NOT NULL, -- Links to the assigned instance
     problem_id UUID NOT NULL, -- The specific problem this submission is for
     student_id UUID NOT NULL,
     code_content TEXT NOT NULL, -- The pasted code
     write_up TEXT, -- The student's explanation
     version INT NOT NULL DEFAULT 1, -- For tracking resubmissions
     created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
     updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

     CONSTRAINT fk_submission_student_assignment FOREIGN KEY (student_assignment_id) REFERENCES student_assignments(id) ON DELETE CASCADE,
     CONSTRAINT fk_submission_problem FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE,
     CONSTRAINT fk_submission_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
     CONSTRAINT uq_submission_version UNIQUE (student_assignment_id, problem_id, version)
);

/*
 * Feedback: A mentor's comments on a *specific submission*.
 * 'line_number' being NULL means it's an "overall comment".
 * This satisfies all your feedback requirements.
*/
CREATE TABLE feedback (
      id UUID PRIMARY KEY,
      submission_id UUID NOT NULL,
      mentor_id UUID NOT NULL,
      comment_text TEXT NOT NULL,
      line_number INT, -- NULLable. If NULL, it's an overall comment.
      created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

      CONSTRAINT fk_feedback_submission FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE,
      CONSTRAINT fk_feedback_mentor FOREIGN KEY (mentor_id) REFERENCES users(id) ON DELETE CASCADE
);
