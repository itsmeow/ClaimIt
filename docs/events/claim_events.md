# Claim Events

These are all events that apply to claims and their happenings.

### Management Events

These events apply to claim management.

##### ClaimAddedEvent
Fires when a claim is added using `ClaimManager#addClaimToListInsecurely(ClaimArea)` (or any methods that call such) <br>
Non-cancelable. Cancel deserialized claims using `ClaimDeserializationEvent` and cancel created claims using `ClaimCreatedEvent`.<br>
This should be used for tracking the claims list seperately. See `ClaimPageTracker` in ClaimIt source.<br>

##### ClaimCreatedEvent
Fires when a claim is added via traditional overlap checking using `ClaimManager#addClaim(ClaimArea)`.<br>
Cancelable. Cancelling prevents the claim from being added.<br>

##### ClaimRemovedEvent
Fires whenever a claim is removed from the list, for any reason except being cleared before initialization.<br>
Canceling prevents a claim from being removed. If you track this, you should also be tracking `ClaimsClearedEvent`.<br>

##### ClaimsClearedEvent.Pre
Fires before the claims list is cleared for any reason. This **DOES NOT** fire any `ClaimRemovedEvent`s. Non-Cancelable.<br>

##### ClaimsClearedEvent.Post
Fires after the claims list is cleared for any reason. This **DOES NOT** fire any `ClaimRemovedEvent`s. Non-Cancelable.<br>

### Serialization Events

##### ClaimSerializationEvent
Fires before a claim is written to NBT. Canceling prevents it from being saved. This will destroy the claim data.<br>
Data is modifiable here, as the it fires just before the claim is written to disk.<br>

##### ClaimDeserializationEvent
Fires after a claim has been read from NBT, before it is added to the claim list.<br>
Data can be modified here. Canceling prevents it from being added to the list, purging the data.<br>

### Misc Claim Events

##### ClaimCheckPermissionEvent
This fires as a permission is being checked on a claim. This is non-cancelable, but has results.<br>
To do nothing, simply do not touch the result or set it to `Result.DEFAULT`<br>
If you wish to allow a permission check (return true), call `event.setResult(Result.ALLOW)`<br>
If you wish to deny a permission check (return false), call `event.setResult(Result.DENY)`<br>