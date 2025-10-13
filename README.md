# Mentorship App — MVP Requirements

A simple mentoring tool where mentors give focused assignments (problems + useful links), mentees submit their code and a short write-up, and mentors give feedback. Start small with **1-to-1 mentoring** only.

---

## 🎯 MVP Goals
1. Make it easy for a mentor to connect with a mentee.
2. Let mentors create assignments and attach helpful links.
3. Let mentees submit pasted code and explain their approach.
4. Let mentors give clear, written feedback.
5. Keep the experience **fast, minimal, distraction-free**.

---

##  Users & Primary Tasks

### Mentor
- Invite/connect with a mentee.
- Create an assignment.
- Add links to resources with a short note explaining why they matter.
- Assign the work to one or more mentees they’re linked with.
- Review submissions and leave feedback (overall comments; optional line notes).

### Mentee
- Accept mentor connection.
- View assigned work with details and links.
- Paste code + write a brief explanation of the approach.
- View feedback history for each assignment.

---

##  Key Flows (Plain Language)
1. **Linking**: Mentor sends an invite → mentee accepts → they see each other in their dashboards.
2. **Assigning**: Mentor writes an assignment, adds links, selects mentee(s), assigns.
3. **Submitting**: Mentee opens assignment, pastes code + write-up, submits; can resubmit new versions.
4. **Feedback**: Mentor opens a submission, writes comments; mentee reads them and iterates if needed.

---

##  Content & Fields (MVP)
- **Assignment**: Title, description, optional tags, optional due date, list of links (URL + short note).
- **Submission**: Pasted code (text), write-up (text), version number, submitted time.
- **Feedback**: Overall comments; optional notes tied to specific line numbers.

---

##  Out of Scope (MVP)
- Cohorts/groups
- Notifications
- File uploads (paste only)
- Analytics/dashboards
- Payments, complex permissions
- Advanced editor/diffs, in-app messaging

---

## ✅ Acceptance Criteria (MVP Sign-off Checklist)

### Linking
- [ ] Mentor can send an invite by email.
- [ ] Mentee can see and accept the invite.
- [ ] Both see each other listed (“My mentor” / “My mentees”).

### Assignments
- [ ] Mentor can create, edit, and archive an assignment.
- [ ] Mentor can add at least one link with a short description.
- [ ] Mentor can assign to **one or more** of their mentees.
- [ ] Mentee sees assigned items in a simple list with a clear status.

### Submissions
- [ ] Mentee can paste code and write a short explanation.
- [ ] Mentee can submit multiple versions; versions are visible in order.
- [ ] Mentee can view their submission history per assignment.

### Feedback
- [ ] Mentor can open a specific submission and write feedback.
- [ ] Mentee can read feedback clearly in the submission view.
- [ ] *(Nice-to-have)* Mentor can add an optional note tied to a line number.

### General
- [ ] Simple sign in/out flows.
- [ ] Basic error messages if something goes wrong.
- [ ] Content is only visible to the linked mentor/mentee pair.
