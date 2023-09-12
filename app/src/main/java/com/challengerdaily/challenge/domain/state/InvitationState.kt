package com.challengerdaily.challenge.domain.state

sealed class InvitationState(val message: String) {
    object Success : InvitationState("Invitation sent successfully to user.")
    object UserAlreadyMember : InvitationState("User is already a member of this group.")
    object RequestAlreadySent : InvitationState("This group has already sent a request to this user. Please wait for the user's response.")
    object UserNotFound : InvitationState("User not found.")
    object NoInvitationPermission: InvitationState("You do not have permission to invite users.")
    object EmptyState: InvitationState("Unknown")
}