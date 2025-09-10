import { Component, computed, input } from '@angular/core';

@Component({
  selector: 'app-main',
  standalone: false,
  
  templateUrl: './main.component.html',
  styleUrl: './main.component.css'
})
export class MainComponent {
  isLeftSidebarCollapsed = input.required<boolean>();
  screenWidth = input.required<number>();
 sizeClass = computed(() => {
  const isLeftSidebarCollapsed = this.isLeftSidebarCollapsed()
    
  if (isLeftSidebarCollapsed) {
    // Kada je sidebar SKLOPLJEN (collapsed) - manji sidebar
    return this.screenWidth() > 768 ? 'body-trimmed' : 'body-md-screen';
    } else {
    // Kada je sidebar RAŠIREN (expanded) - širok sidebar
    return '';
    }
  })
}
