/*
 * Property of rednifhctilg, no copying!!!
 */

<?php
if(has_perms($player)) {
	set_perms($player);
}

if($dragon !== $spawned) {
	spawn($dragon);
	fight($player);
}
