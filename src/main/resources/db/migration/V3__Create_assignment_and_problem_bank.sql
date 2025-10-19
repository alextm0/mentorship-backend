/*
 * Problems: A mentor's reusable bank of problems.
 * 'in_bank' could be used for a mentor's private library vs. shared.
*/
CREATE TABLE problems (
      id UUID PRIMARY KEY,
      title VARCHAR(100) NOT NULL,
      description TEXT,
      difficulty VARCHAR(20) NOT NULL CHECK (difficulty IN ('easy', 'medium', 'hard')),
      external_url VARCHAR(255),
      mentor_id UUID NOT NULL, -- The author
      in_bank BOOLEAN NOT NULL DEFAULT FALSE,
      created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
      updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

      CONSTRAINT fk_problem_mentor FOREIGN KEY (mentor_id) REFERENCES users(id) ON DELETE CASCADE
);

/*
 * Assignments: The 'template' for an assignment.
 * This holds the title and description, but NOT who it's assigned to.
*/
CREATE TABLE assignments (
     id UUID PRIMARY KEY,
     mentor_id UUID NOT NULL, -- The author
     title VARCHAR(100) NOT NULL,
     description TEXT,
     tags VARCHAR(50)[], -- Optional tags from your requirements
     created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
     updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

     CONSTRAINT fk_assignment_mentor FOREIGN KEY (mentor_id) REFERENCES users(id) ON DELETE CASCADE
);

/*
 * Assignment Links: Stores the "useful links (URL + short note)"
 * for a specific assignment template.
*/
CREATE TABLE assignment_links (
      id UUID PRIMARY KEY,
      assignment_id UUID NOT NULL,
      url VARCHAR(255) NOT NULL,
      note VARCHAR(255),
      created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

      CONSTRAINT fk_link_assignment FOREIGN KEY (assignment_id) REFERENCES assignments(id) ON DELETE CASCADE
);

/*
 * Assignment Problem Links: Join table to link reusable problems
 * to a specific assignment template.
*/
CREATE TABLE assignment_problem_links (
      id UUID PRIMARY KEY,
      assignment_id UUID NOT NULL,
      problem_id UUID NOT NULL,

      CONSTRAINT uq_assignment_problem UNIQUE (assignment_id, problem_id),
      CONSTRAINT fk_apl_assignment FOREIGN KEY (assignment_id) REFERENCES assignments(id) ON DELETE CASCADE,
      CONSTRAINT fk_apl_problem FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE
);
